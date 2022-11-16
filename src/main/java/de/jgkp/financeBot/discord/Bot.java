package de.jgkp.financeBot.discord;

import de.jgkp.financeBot.config.Configuration;
import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Getter
@Setter
public class Bot {

    private Configuration configuration;
    private JDA jda;
    private BotCommands botCommands;

    @Autowired
    public Bot(Configuration configuration, BotCommands botCommands) {
        this.configuration = configuration;
        this.botCommands = botCommands;
    }

    public void startBot() throws InterruptedException {

        JDA jda = JDABuilder.createDefault(configuration.getBotToken())
                .setActivity(Activity.playing("/help"))
                .addEventListeners(botCommands)
                .enableIntents(GatewayIntent.GUILD_MEMBERS)
                .setMemberCachePolicy(MemberCachePolicy.ALL)
                .build()
                .awaitReady();

        System.out.println("Im online.");

        setJda(jda);

        updateCommands();

        System.out.println("Updated Commands");
    }



    public void updateCommands() {
        //Global
        List<CommandData> commandData = new ArrayList<>();

        commandData.add(Commands.slash("help", "Du brauchst Hilfe bei der Benutzung des Bots oder hast Fragen? Schau zuerst hier nach!"));
        commandData.add(Commands.slash("meine-mitgliedschaft", "Sendet dir alle Informationen über deine persönliche 1JGKP-Mitgliedschaft per DM"));
        commandData.add(Commands.slash("mitgliedschaftsinfos", "Sendet dir allgemeine Informationen über die 1JGKP-Mitgliedschaft per DM"));

        jda.updateCommands().addCommands(commandData).queue();
    }
}
