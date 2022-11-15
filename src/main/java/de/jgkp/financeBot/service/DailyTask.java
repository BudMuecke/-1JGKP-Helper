package de.jgkp.financeBot.service;

import de.jgkp.financeBot.db.entities.Accounts;
import de.jgkp.financeBot.db.repositories.AccountsRepository;
import de.jgkp.financeBot.db.repositories.SettingsRepository;
import de.jgkp.financeBot.discord.DiscordReminderEvents;
import de.jgkp.financeBot.discord.DiscordReminderWithoutEvents;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DailyTask {
    private AccountsRepository accountsRepository;
    private SettingsRepository settingsRepository;
    private DiscordReminderWithoutEvents discordReminderWithoutEvents;

    @Autowired
    public DailyTask(AccountsRepository accountsRepository, SettingsRepository settingsRepository, DiscordReminderWithoutEvents discordReminderWithoutEvents) {
        this.accountsRepository = accountsRepository;
        this.settingsRepository = settingsRepository;
        this.discordReminderWithoutEvents = discordReminderWithoutEvents;
    }

    public void updateAccounts(){
        List<Accounts> accountsList = accountsRepository.findAll();

        for (int i = 0; i < accountsList.size(); i++) {
            Accounts accounts = accountsList.get(i);

            if(accounts.getMembershipDaysLeft() - 1 >= 0) {
                accounts.setMembershipDaysLeft(accounts.getMembershipDaysLeft() - 1);
                accounts.setCurrentAccount(accounts.getCurrentAccount() - settingsRepository.findSettingsById(1L).getDailyMembershipFee());
            }

            if(accounts.getMembershipDaysLeft() - 1 < 0){
                accounts.setMembershipDaysLeft(0.0);
                accounts.setCurrentAccount(0.0);
            }

            double maxDays = settingsRepository.findSettingsById(1L).getMembershipExpiresInXDaysReminderDaysAmount() + 0.00;
            double minDays = settingsRepository.findSettingsById(1L).getMembershipExpiresInXDaysReminderDaysAmount() + 0.99;

            if (accounts.getMembershipDaysLeft() <= minDays && accounts.getMembershipDaysLeft() >= maxDays){
                discordReminderWithoutEvents.remindUserXDaysLeft(String.valueOf(accounts.getUserId()));
            }

            if (accounts.getMembershipDaysLeft() <= 0 && !accounts.getHasRecievedMembershipExpiredReminder()){
                discordReminderWithoutEvents.remindUserMembershipExpired(String.valueOf(accounts.getUserId()));
                accounts.setHasRecievedMembershipExpiredReminder(true);
                discordReminderWithoutEvents.remindLeaderMembershipExpired(accounts.getUserId());
            }
            accountsRepository.save(accounts);
        }
    }
}
