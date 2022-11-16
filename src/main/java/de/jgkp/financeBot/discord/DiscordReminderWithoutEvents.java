package de.jgkp.financeBot.discord;

import de.jgkp.financeBot.db.entities.Accounts;
import de.jgkp.financeBot.db.repositories.AccountsRepository;
import de.jgkp.financeBot.db.repositories.SettingsRepository;
import net.dv8tion.jda.api.exceptions.ErrorHandler;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.requests.ErrorResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class DiscordReminderWithoutEvents {

    private Bot bot;
    private Embeds embeds;

    private SettingsRepository settingsRepository;

    private AccountsRepository accountsRepository;

    @Autowired
    public DiscordReminderWithoutEvents(Bot bot, Embeds embeds, SettingsRepository settingsRepository, AccountsRepository accountsRepository) {
        this.bot = bot;
        this.embeds = embeds;
        this.settingsRepository = settingsRepository;
        this.accountsRepository = accountsRepository;
    }

    public void remindUserXDaysLeft(String userID) {

        bot.getJda().retrieveUserById(userID).queue(user -> {
            String username = user.getName();
            user.openPrivateChannel()
                    .flatMap(channel -> channel.sendMessageEmbeds(embeds.createEmbedXDaysLeftReminder(username).build()).addActionRow(
                            Button.primary("Mitgliedschaftsinfos", "Mitgliedschaftsinfos"))
                    )
                    .queue(null, new ErrorHandler()
                            .ignore(ErrorResponse.UNKNOWN_MESSAGE)
                            .handle(
                                    ErrorResponse.CANNOT_SEND_TO_USER,
                                    (e) -> bot.getJda().getTextChannelById(settingsRepository.findSettingsById(1L).getLeaderChannelId()).sendMessageEmbeds(embeds.createEmbedCantWriteUser(username, "Erinnerung: Mitgliedschaft läuft in X Tagen aus").build()).queue()));
        });
    }

    public void remindUserMembershipExpired(String userId) {
        bot.getJda().retrieveUserById(userId).queue(user -> {
            String username = user.getName();
            user.openPrivateChannel()
                    .flatMap(channel -> channel.sendMessageEmbeds(embeds.createEmbedExpiredReminder(username).build()).addActionRow(
                            Button.primary("Mitgliedschaftsinfos", "Mitgliedschaftsinfos"))
                    )
                    .queue(null, new ErrorHandler()
                            .ignore(ErrorResponse.UNKNOWN_MESSAGE)
                            .handle(
                                    ErrorResponse.CANNOT_SEND_TO_USER,
                                    (e) -> bot.getJda().getTextChannelById(settingsRepository.findSettingsById(1L).getLeaderChannelId()).sendMessageEmbeds(embeds.createEmbedCantWriteUser(username, "Erinnerung: Mitgliedschaft ist ausgelaufen").build()).queue()));
        });
    }

    public void remindLeaderMembershipExpired(long userId) {

        String leaderChannel = settingsRepository.findSettingsById(1L).getLeaderChannelId();

        bot.getJda().retrieveUserById(userId).queue(user -> {
            if (bot.getJda().getTextChannelById(leaderChannel).canTalk()) {
                bot.getJda().getTextChannelById(leaderChannel).sendMessageEmbeds(embeds.createEmbedRemindLeader(user.getName()).build()).addActionRow(
                        Button.link("https://dsg-gaming.de/admin/adminconfigs/1/edit", "DSG Website"), Button.primary("Bestätige Entfernung", "Bestätige Entfernung")
                ).queue((message) -> {
                    Accounts accounts = accountsRepository.findAccountsByUserId(userId);
                    accounts.setLeaderReminderMessageId(message.getIdLong());
                    accountsRepository.save(accounts);
                });
                System.out.println("Send an expired reminder to " + bot.getJda().getTextChannelById(leaderChannel).getName());
            }
        });
    }

    public void remindRecruitmentCandidateTimeEnded(long userId, String status) {

        String recruitmentChannel = settingsRepository.findSettingsById(1L).getRecruitmentChannelId();

        bot.getJda().retrieveUserById(userId).queue(user -> {
            if (bot.getJda().getTextChannelById(recruitmentChannel).canTalk()) {
                bot.getJda().getTextChannelById(recruitmentChannel).sendMessageEmbeds(embeds.createEmbedCandidateTimeEnded(user.getName(), status).build())
                .queue();
            }
        });
    }
}
