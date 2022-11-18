package de.jgkp.financeBot.discord;

import de.jgkp.financeBot.db.entities.Accounts;
import de.jgkp.financeBot.db.repositories.AccountsRepository;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class DiscordReminderEvents {

    private Embeds embeds;
    private AccountsRepository accountsRepository;

    @Autowired
    public DiscordReminderEvents(Embeds embeds, AccountsRepository accountsRepository) {
        this.embeds = embeds;
        this.accountsRepository = accountsRepository;
    }

    public void sendConfirmMessage(SlashCommandInteractionEvent event, long userId) {
        event.getHook().getJDA().retrieveUserById(userId)
                .map(User::getName)
                .queue(name -> {
                    event.getHook().sendMessageEmbeds(embeds.createEmbedConfirmMessage(
                            name).build()).setEphemeral(false).addActionRow(
                            Button.link("https://dsg-gaming.de/admin/adminconfigs/1/edit", "DSG Website"), Button.primary("Bestätige Vergabe", "Bestätige Vergabe")
                    ).queue((message) -> {
                        Accounts accounts = accountsRepository.findAccountsByUserId(userId);
                        accounts.setLeaderReminderMessageId(message.getIdLong());
                        accountsRepository.save(accounts);
                    });
                });
    }
}
