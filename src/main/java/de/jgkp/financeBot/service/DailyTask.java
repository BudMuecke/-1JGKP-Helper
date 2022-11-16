package de.jgkp.financeBot.service;

import de.jgkp.financeBot.db.entities.Accounts;
import de.jgkp.financeBot.db.entities.Candidate;
import de.jgkp.financeBot.db.repositories.AccountsRepository;
import de.jgkp.financeBot.db.repositories.CandidateRepository;
import de.jgkp.financeBot.db.repositories.SettingsRepository;
import de.jgkp.financeBot.discord.DiscordReminderEvents;
import de.jgkp.financeBot.discord.DiscordReminderWithoutEvents;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
public class DailyTask {
    private AccountsRepository accountsRepository;
    private SettingsRepository settingsRepository;
    private DiscordReminderWithoutEvents discordReminderWithoutEvents;
    private CandidateRepository candidateRepository;
    private Services services;

    @Autowired
    public DailyTask(AccountsRepository accountsRepository, SettingsRepository settingsRepository, DiscordReminderWithoutEvents discordReminderWithoutEvents, CandidateRepository candidateRepository, Services services) {
        this.accountsRepository = accountsRepository;
        this.settingsRepository = settingsRepository;
        this.discordReminderWithoutEvents = discordReminderWithoutEvents;
        this.candidateRepository = candidateRepository;
        this.services = services;
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

    public void checkCandidates(){
        List<Candidate> candidateList = candidateRepository.findAll();

        for (Candidate candidate : candidateList) {
            if (Objects.equals(String.valueOf(services.getCurrentDate()), candidate.getEndDate())) {
                discordReminderWithoutEvents.remindRecruitmentCandidateTimeEnded(candidate.getUserId(), candidate.getStatus());
                candidateRepository.delete(candidate);
            }
        }
    }
}
