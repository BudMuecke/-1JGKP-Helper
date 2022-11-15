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
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
        Guild guild = jda.getGuildById(configuration.getGuildId());

        //Server-Only
        if (guild != null) {

            CommandListUpdateAction commands = guild.updateCommands();

            commands.addCommands(Commands.slash("admin-zahlung-hinzufügen", "Fügt dem Benutzerkonto eine neue Zahlung hinzu").setDefaultPermissions(DefaultMemberPermissions.DISABLED)
                            .addOptions(
                                    new OptionData(OptionType.USER, "benutzer", "Der Nutzer, der die Zahlung geleistet hat", true),
                                    new OptionData(OptionType.STRING, "zahlungsdatum", "Das Datum, an dem der Nutzer die Zahlung geleistet hat. Format: tt.MM.jjjj", true),
                                    new OptionData(OptionType.NUMBER, "betrag", "Der Geldbetrag, den der Nutzer bezahlt hat. Format: xxx,xx", true).setRequiredRange(0.01, 9007199254740991.000000)))
                    .queue();

            commands.addCommands(Commands.slash("admin-bekomme-benutzerdaten", "Sendet alle Benutzerdaten je nach gewählten Optionen").setDefaultPermissions(DefaultMemberPermissions.DISABLED)
                            .addOptions(
                                    new OptionData(OptionType.USER, "benutzer", "Der Benutzer, von dem du die Informationen erhalten möchtest", false),
                                    new OptionData(OptionType.INTEGER, "minimalanzahl-tage-verbleibend", "Die minimale Anzahl an Tagen, die die Mitgliedschaft noch läuft", false),
                                    new OptionData(OptionType.INTEGER, "maximalanzahl-tage-verbleibend", "Die maximale Anzahl an Tagen, die die Mitgliedschaft noch läuft", false)))
                    .queue();
            commands.addCommands(Commands.slash("admin-zeige-einstellungen", "Sendet alle aktuellen Einstellungen").setDefaultPermissions(DefaultMemberPermissions.DISABLED))
                    .queue();
            commands.addCommands(Commands.slash("admin-ändere-einstellungen", "Ändert die aktuellen Einstellungen").setDefaultPermissions(DefaultMemberPermissions.DISABLED)
                            .addOptions(
                                    new OptionData(OptionType.NUMBER, "tägliche-mitgliedschaftsgebühr", "Die Mitglieschaftsgebühr, die täglich erhoben wird. Format: xxx.xx", false).setRequiredRange(0.01, 9007199254740991.000000),
                                    new OptionData(OptionType.INTEGER, "erinnerung-x-tage-vor-ablauf", "Die Anzahl an Tagen, bei denen die erste Erinnerung gesendet werden soll", false),
                                    new OptionData(OptionType.STRING, "spam-kanal-id", "Die ID des Textkanals in den der Bot die Antworten senden soll", false),
                                    new OptionData(OptionType.STRING, "benachrichtigungskanal-id", "Die ID des Textkanals in den der Bot Benachrichtigungen senden soll", false)))
                    .queue();
            commands.addCommands(Commands.slash("admin-schreibe-laufzeit-gut", "Schreibt einem Benutzer eine bestimmte Laufzeit gut").setDefaultPermissions(DefaultMemberPermissions.DISABLED)
                            .addOptions(
                                    new OptionData(OptionType.USER, "benutzer", "Der Nutzern dem die Laufzeit gutgeschrieben werde soll", true),
                                    new OptionData(OptionType.INTEGER, "laufzeit-in-monate", "Die Laufzeit, die gutgeschrieben werden soll in Monate", true)))
                    .queue();
            commands.addCommands(Commands.slash("verschenke-mitgliedschaft", "Schenke einem Nutzer einen Teil deiner Mitgliedschaftslaufzeit.")
                            .addOptions(
                                    new OptionData(OptionType.USER, "benutzer", "Der Nutzern, dem du einen Teil deiner Mitgliedschaftslaufzeit schenken möchtest", true),
                                    new OptionData(OptionType.INTEGER, "laufzeit-in-monate", "Die Laufzeit, die übertragen werden soll in Monate", true)))
                    .queue();
            commands.queue();
        }

        //Global
        CommandListUpdateAction commands = jda.updateCommands();

        commands.addCommands(Commands.slash("help", "Du brauchst Hilfe bei der Benutzung des Bots oder hast Fragen? Schau zuerst hier nach!")).queue();
        commands.addCommands(Commands.slash("meine-mitgliedschaft", "Sendet dir alle Informationen über deine persönliche 1JGKP-Mitgliedschaft per DM")).queue();
        commands.addCommands(Commands.slash("mitgliedschaftsinfos", "Sendet dir allgemeine Informationen über die 1JGKP-Mitgliedschaft per DM")).queue();
        commands.queue();
    }
}
