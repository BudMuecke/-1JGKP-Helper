package de.jgkp.financeBot.discord;

import de.jgkp.financeBot.db.entities.Accounts;
import de.jgkp.financeBot.db.entities.Settings;
import de.jgkp.financeBot.db.repositories.AccountsRepository;
import de.jgkp.financeBot.db.repositories.SettingsRepository;
import de.jgkp.financeBot.service.Services;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.PrivateChannel;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.exceptions.ErrorHandler;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.requests.ErrorResponse;
import net.dv8tion.jda.api.requests.RestAction;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.swing.plaf.basic.BasicInternalFrameTitlePane;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

@Component
public class BotCommands extends ListenerAdapter {

    private AccountsRepository accountsRepository;
    private Services services;
    private DiscordReminderEvents discordReminderEvents;
    private SettingsRepository settingsRepository;
    private Embeds embeds;

    @Autowired
    public BotCommands(AccountsRepository accountsRepository, Services services, DiscordReminderEvents discordReminderEvents, SettingsRepository settingsRepository, Embeds embeds) {
        this.accountsRepository = accountsRepository;
        this.services = services;
        this.discordReminderEvents = discordReminderEvents;
        this.settingsRepository = settingsRepository;
        this.embeds = embeds;
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (event.getName().equals("admin-zahlung-hinzufügen")) {
            OptionMapping userOption = event.getOption("benutzer");
            OptionMapping dateOption = event.getOption("zahlungsdatum");
            OptionMapping amountOption = event.getOption("betrag");

            if (userOption == null) {
                event.deferReply().queue();
                event.getHook().sendMessage("Fehler: Benutzer wurde nicht angegeben.").setEphemeral(true).queue();
            }
            if (dateOption == null) {
                event.deferReply().queue();
                event.getHook().sendMessage("Fehler: Zahlungsdatum wurde nicht angegeben.").setEphemeral(true).queue();
            }
            if (amountOption == null) {
                event.deferReply().queue();
                event.getHook().sendMessage("Fehler: Betrag wurde nicht angegeben.").setEphemeral(true).queue();
            }

            assert userOption != null;
            User user = userOption.getAsUser();

            assert dateOption != null;
            String date = dateOption.getAsString();

            assert amountOption != null;
            double amount = amountOption.getAsDouble();

            boolean isUserExisting = accountsRepository.existsByUserId(user.getIdLong());

            try {
                if (services.calcDayDifference(date) == -1) {
                    event.reply("Das Zahlungsdatum kann nur ein vergangenes oder heutiges Datum sein! Bitte versuche es erneut.").setEphemeral(true).queue();
                    return;
                }
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }

            if (isUserExisting) {
                Accounts accounts = accountsRepository.findAccountsByUserId(user.getIdLong());
                accounts.setCurrentAccount(accounts.getCurrentAccount() + amount);

                double daysLeft = amount / settingsRepository.findSettingsById(1L).getDailyMembershipFee();

                accounts.setMembershipDaysLeft(accounts.getMembershipDaysLeft() + daysLeft);
                accounts.setLastPayment(date);
                accounts.setLastPaymentAmount(amount);
                accounts.setHasRecievedMembershipExpiredReminder(false);
                accountsRepository.save(accounts);

                if (!accounts.getHasReservedSlot()) {
                    discordReminderEvents.sendConfirmMessage(event, user.getIdLong());
                }
            } else {
                Accounts accounts = new Accounts();
                accounts.setCurrentAccount(amount);
                accounts.setUserId(user.getIdLong());

                double daysLeft = amount / settingsRepository.findSettingsById(1L).getDailyMembershipFee();

                accounts.setHasRecievedMembershipExpiredReminder(false);
                accounts.setHasReservedSlot(false);
                accounts.setMembershipDaysLeft(daysLeft);
                accounts.setUserName(user.getName());
                accounts.setLastPaymentAmount(amount);
                accounts.setLastPayment(date);
                accountsRepository.save(accounts);

                if (!accounts.getHasReservedSlot()) {
                    discordReminderEvents.sendConfirmMessage(event, user.getIdLong());
                }
            }
            event.deferReply().queue();
            String reviser = event.getUser().getName();
            String newAmount = String.format(Locale.GERMAN, "%,.2f", amount);
            event.getJDA().getTextChannelById(settingsRepository.findSettingsById(1L).getLeaderChannelId()).sendMessageEmbeds(embeds.createEmbedConfirmedPayment(user.getName(), newAmount, date, reviser).build()).queue();
        } else if (event.getName().equals("meine-mitgliedschaft")) {
            Accounts accounts = accountsRepository.findAccountsByUserId(event.getUser().getIdLong());

            if (accounts == null) {

                String username = event.getUser().getName();
                event.getUser().openPrivateChannel()
                        .flatMap(channel -> channel.sendMessageEmbeds(embeds.createEmbedNoRegisteredPayments(username).build()).addActionRow(
                                Button.primary("Mitgliedschaftsinfos", "Mitgliedschaftsinfos")))
                        .flatMap(reply -> event.reply("Die angefragten Informationen wurden dir per DM gesendet").setEphemeral(true))
                        .queue(null, new ErrorHandler()
                                .ignore(ErrorResponse.UNKNOWN_MESSAGE)
                                .handle(
                                        ErrorResponse.CANNOT_SEND_TO_USER,
                                        (e) -> event.reply("Deine Privatsphäreeinstellungen verhindern, dass ich dir Direktnachrichten senden kann. \n \n " +
                                                "Bitte ändere das: Klicke oben links auf den Servernamen --> Klicke auf Privatsphäreeinstellungen --> Klicke auf Direktnachrichten").setEphemeral(true).queue()));

            } else {
                String name = accounts.getUserName();
                String lastPayment = accounts.getLastPayment();
                Double amount = accounts.getLastPaymentAmount();
                DecimalFormat df = new DecimalFormat("#.00");
                String stringAmount = df.format(amount);
                double daysLeft = accounts.getMembershipDaysLeft();
                daysLeft = Math.round(100.0 * daysLeft) / 100.0;
                boolean hasReservedSlot = accounts.getHasReservedSlot();
                String stringHasReservedSlot;

                if (!hasReservedSlot) {
                    stringHasReservedSlot = "nein";
                } else {
                    stringHasReservedSlot = "ja";
                }

                int newDaysLeft = (int) daysLeft;
                String enddate = services.calcEndDate(newDaysLeft);

                event.getUser().openPrivateChannel()
                        .flatMap(channel -> channel.sendMessageEmbeds(embeds.createEmbedMyMembership(name, lastPayment, stringAmount, String.valueOf(newDaysLeft), stringHasReservedSlot, enddate).build()).addActionRow(
                                Button.primary("Mitgliedschaftsinfos", "Mitgliedschaftsinfos")))
                        .flatMap(reply -> event.reply("Die angefragten Informationen wurden dir per DM gesendet").setEphemeral(true))
                        .queue(null, new ErrorHandler()
                                .ignore(ErrorResponse.UNKNOWN_MESSAGE)
                                .handle(
                                        ErrorResponse.CANNOT_SEND_TO_USER,
                                        (e) -> event.reply("Deine Privatsphäreeinstellungen verhindern, dass ich dir Direktnachrichten senden kann. \n \n " +
                                                "Bitte ändere das: Klicke oben links auf den Servernamen --> Klicke auf Privatsphäreeinstellungen --> Klicke auf Direktnachrichten").setEphemeral(true).queue()));

            }
        } else if (event.getName().equals("help")) {
            String username = event.getUser().getName();

            event.getUser().openPrivateChannel()
                    .flatMap(channel -> channel.sendMessageEmbeds(embeds.createEmbedHelpMessage(username).build()).addActionRow(
                            Button.link("https://docs.google.com/document/d/1txxEsR7xS_m1_Q4cmwVSJhCaPt0tsRb53HztYfwk8lA/edit?usp=sharing", "Zur Doku")))
                    .flatMap(reply -> event.reply("Die angefragten Informationen wurden dir per DM gesendet").setEphemeral(true))
                    .queue(null, new ErrorHandler()
                            .ignore(ErrorResponse.UNKNOWN_MESSAGE)
                            .handle(
                                    ErrorResponse.CANNOT_SEND_TO_USER,
                                    (e) -> event.reply("Deine Privatsphäreeinstellungen verhindern, dass ich dir Direktnachrichten senden kann. \n \n " +
                                            "Bitte ändere das: Klicke oben links auf den Servernamen --> Klicke auf Privatsphäreeinstellungen --> Klicke auf Direktnachrichten").setEphemeral(true).queue()));

        } else if (event.getName().equals("mitgliedschaftsinfos")) {

            event.getUser().openPrivateChannel()
                    .flatMap(channel -> channel.sendMessageEmbeds(embeds.createEmbedMembership().build()).addActionRow(
                            Button.link("https://docs.google.com/document/d/1txxEsR7xS_m1_Q4cmwVSJhCaPt0tsRb53HztYfwk8lA/edit?usp=sharing", "Zur Doku")))
                    .flatMap(reply -> event.reply("Die angefragten Informationen wurden dir per DM gesendet").setEphemeral(true))
                    .queue(null, new ErrorHandler()
                            .ignore(ErrorResponse.UNKNOWN_MESSAGE)
                            .handle(
                                    ErrorResponse.CANNOT_SEND_TO_USER,
                                    (e) -> event.reply("Deine Privatsphäreeinstellungen verhindern, dass ich dir Direktnachrichten senden kann. \n \n " +
                                            "Bitte ändere das: Klicke oben links auf den Servernamen --> Klicke auf Privatsphäreeinstellungen --> Klicke auf Direktnachrichten").setEphemeral(true).queue()));

        } else if (event.getName().equals("admin-bekomme-benutzerdaten")) {
            OptionMapping userOption = event.getOption("benutzer");
            OptionMapping minimumDaysOption = event.getOption("minimalanzahl-tage-verbleibend");
            OptionMapping maximumDaysOption = event.getOption("maximalanzahl-tage-verbleibend");

            if (userOption == null && minimumDaysOption == null && maximumDaysOption == null) {

                List<Accounts> accountsList = accountsRepository.findAll();

                extractDataFromList(event, accountsList);

            } else if (userOption == null && minimumDaysOption == null) {

                double maximumDays = maximumDaysOption.getAsDouble();
                List<Accounts> accountsList = accountsRepository.findAllByMembershipDaysLeftLessThanEqual(maximumDays);

                extractDataFromList(event, accountsList);
            } else if (userOption == null && maximumDaysOption == null) {

                double minimumDays = minimumDaysOption.getAsDouble();
                List<Accounts> accountsList = accountsRepository.findAllByMembershipDaysLeftGreaterThanEqual(minimumDays);

                extractDataFromList(event, accountsList);

            } else if (userOption != null) {

                User user = userOption.getAsUser();

                Accounts accounts = accountsRepository.findAccountsByUserId(user.getIdLong());
                if (accounts == null) {
                    event.reply("Dieser Benutzer ist nicht in der Datenbank vorhanden.").setEphemeral(true).queue();
                    return;
                }

                extractDataFromSingleAccount(event, accounts);
                event.reply("Die angefragten Informationen wurden in " + event.getJDA().getTextChannelById(settingsRepository.findSettingsById(1L).getHelperSpamChannelId()).getName() + " gesendet").setEphemeral(true).queue();
            } else if (userOption == null && maximumDaysOption != null && minimumDaysOption != null) {

                double maximumDays = maximumDaysOption.getAsDouble();
                double minimumDays = minimumDaysOption.getAsDouble();
                List<Accounts> accountsList = accountsRepository.findAllByMembershipDaysLeftGreaterThanEqualAndMembershipDaysLeftLessThanEqual(minimumDays, maximumDays);

                if (accountsList.size() == 0) {
                    event.reply("Es gibt keinen Benutzer in der Datenbank, auf den der angegebene Zeitrahmen zutrifft.").setEphemeral(true).queue();
                    return;
                }
                extractDataFromList(event, accountsList);
            }
        } else if (event.getName().equals("admin-zeige-einstellungen")) {
            Settings settings = settingsRepository.findSettingsById(1L);
            String notificationChannelName = event.getJDA().getTextChannelById(settings.getLeaderChannelId()).getName();
            String spamChannelName = event.getJDA().getTextChannelById(settings.getHelperSpamChannelId()).getName();
            String notificationChannelId = settings.getLeaderChannelId();
            String spamChannelId = settings.getHelperSpamChannelId();
            int membershipEndsInXDaysReminder = settings.getMembershipExpiresInXDaysReminderDaysAmount();
            double dalyMembershipFee = settings.getDailyMembershipFee();

            event.getJDA().getTextChannelById(settingsRepository.findSettingsById(1L).getHelperSpamChannelId()).sendMessageEmbeds(embeds.createEmbedSettings(dalyMembershipFee, membershipEndsInXDaysReminder, notificationChannelId, spamChannelId, notificationChannelName, spamChannelName).build()).queue();
            event.reply("Die angefragten Informationen wurden in " + event.getJDA().getTextChannelById(settingsRepository.findSettingsById(1L).getHelperSpamChannelId()).getName() + " gesendet").setEphemeral(true).queue();
        } else if (event.getName().equals("admin-ändere-einstellungen")) {
            OptionMapping dailyMembershipFee = event.getOption("tägliche-mitgliedschaftsgebühr");
            OptionMapping reminderDays = event.getOption("erinnerung-x-tage-vor-ablauf");
            OptionMapping spamChannelId = event.getOption("spam-kanal-id");
            OptionMapping notificationChannelId = event.getOption("benachrichtigungskanal-id");

            Settings settings = settingsRepository.findSettingsById(1L);

            if (dailyMembershipFee != null) {
                settings.setDailyMembershipFee(dailyMembershipFee.getAsDouble());
            }

            if (reminderDays != null) {
                settings.setMembershipExpiresInXDaysReminderDaysAmount(reminderDays.getAsInt());
            }

            if (spamChannelId != null) {
                settings.setHelperSpamChannelId(spamChannelId.getAsString());
            }

            if (notificationChannelId != null) {
                settings.setLeaderChannelId(notificationChannelId.getAsString());
            }
            settingsRepository.save(settings);
            event.reply("Du hast die Einstellungen erfolgreich geändert").setEphemeral(true).queue();
        } else if (event.getName().equals("admin-schreibe-laufzeit-gut")) {
            OptionMapping userOption = event.getOption("benutzer");
            OptionMapping runtimeOption = event.getOption("laufzeit-in-monate");

            assert userOption != null;
            User user = userOption.getAsUser();
            assert runtimeOption != null;
            int runtime = runtimeOption.getAsInt();

            convertRuntime(event, user, runtime);
            event.reply("Die Laufzeit wurde erfolgreich angerechnet").setEphemeral(true).queue();
            event.getJDA().getTextChannelById(settingsRepository.findSettingsById(1L).getLeaderChannelId()).sendMessageEmbeds(embeds.createEmbedAddRuntime(event.getUser().getName(), user.getName(), runtime).build()).queue();
        } else if (event.getName().equals("verschenke-mitgliedschaft")) {
            OptionMapping userOption = event.getOption("benutzer");
            OptionMapping runtimeOption = event.getOption("laufzeit-in-monate");
            User eventUser = event.getUser();

            assert userOption != null;
            User user = userOption.getAsUser();
            assert runtimeOption != null;
            int runtime = runtimeOption.getAsInt();

            if (accountsRepository.existsByUserId(eventUser.getIdLong())) {
                Accounts eventUserAccount = accountsRepository.findAccountsByUserId(eventUser.getIdLong());

                if (eventUserAccount.getMembershipDaysLeft() >= runtime * 30.4166666667) {
                    eventUserAccount.setMembershipDaysLeft(eventUserAccount.getMembershipDaysLeft() - (runtime * 30.4166666667));
                    eventUserAccount.setCurrentAccount(eventUserAccount.getCurrentAccount() - ((runtime * 30.4166666667) * settingsRepository.findSettingsById(1L).getDailyMembershipFee()));
                    accountsRepository.save(eventUserAccount);

                    convertRuntime(event, user, runtime);
                    event.reply("Die Laufzeit wurde erfolgreich übertragen").setEphemeral(true).queue();
                    event.getJDA().getTextChannelById(settingsRepository.findSettingsById(1L).getLeaderChannelId()).sendMessageEmbeds(embeds.createEmbedGiftRuntime(eventUser.getName(), user.getName(), runtime).build()).queue();
                } else {
                    event.reply("Deine Mitgliedschaft läuft nicht mehr so lange, wie du verschenken möchtest. Bitte versuche es noch einmal.").setEphemeral(true).queue();
                }
            } else {
                event.reply("Du hast keine 1JGKP Mitgliedschaft. Nutze /mitgliedschaftsinfos um mehr zu erfahren").setEphemeral(true).queue();
            }
        }else if (event.getName().equals("recruiter-erstelle-erinnerung")) {

        }
    }

    private void convertRuntime(SlashCommandInteractionEvent event, User user, int runtime) {
        if (accountsRepository.existsByUserId(user.getIdLong())) {
            Accounts accounts = accountsRepository.findAccountsByUserId(user.getIdLong());
            accounts.setMembershipDaysLeft(accounts.getMembershipDaysLeft() + (runtime * 30.4166666667));
            accounts.setCurrentAccount(accounts.getCurrentAccount() + ((runtime * 30.4166666667) * settingsRepository.findSettingsById(1L).getDailyMembershipFee()));
            accounts.setHasRecievedMembershipExpiredReminder(false);
            accountsRepository.save(accounts);

            if (!accounts.getHasReservedSlot()) {
                discordReminderEvents.sendConfirmMessage(event, user.getIdLong());
            }
        } else {
            Accounts accounts = new Accounts();
            accounts.setCurrentAccount((runtime * 30.4166666667) * settingsRepository.findSettingsById(1L).getDailyMembershipFee());
            accounts.setUserId(user.getIdLong());

            double daysLeft = runtime * 30.4166666667;

            accounts.setHasRecievedMembershipExpiredReminder(false);
            accounts.setHasReservedSlot(false);
            accounts.setMembershipDaysLeft(daysLeft);
            accounts.setUserName(user.getName());
            accounts.setLastPaymentAmount(0.00);
            accounts.setLastPayment("nicht vorhanden");
            accountsRepository.save(accounts);

            if (!accounts.getHasReservedSlot()) {
                discordReminderEvents.sendConfirmMessage(event, user.getIdLong());
            }
        }
    }

    private void extractDataFromSingleAccount(SlashCommandInteractionEvent event, Accounts accounts) {
        String name = accounts.getUserName();
        String lastPayment = accounts.getLastPayment();
        Double amount = accounts.getLastPaymentAmount();
        DecimalFormat df = new DecimalFormat("0.00");
        String stringAmount = df.format(amount);
        double daysLeft = accounts.getMembershipDaysLeft();
        daysLeft = Math.round(100.0 * daysLeft) / 100.0;
        boolean hasReservedSlot = accounts.getHasReservedSlot();
        String stringHasReservedSlot;

        if (!hasReservedSlot) {
            stringHasReservedSlot = "nein";
        } else {
            stringHasReservedSlot = "ja";
        }

        int newDaysLeft = (int) daysLeft;
        String enddate = services.calcEndDate(newDaysLeft);

        event.getJDA().getTextChannelById(settingsRepository.findSettingsById(1L).getHelperSpamChannelId()).sendMessageEmbeds(embeds.createEmbedMyMembership(name, lastPayment, stringAmount, String.valueOf(newDaysLeft), stringHasReservedSlot, enddate).build()).queueAfter(1,TimeUnit.SECONDS);
    }

    private void extractDataFromList(SlashCommandInteractionEvent event, List<Accounts> accountsList) {
        for (int i = 0; i < accountsList.size(); i++) {
            Accounts accounts = accountsList.get(i);
            extractDataFromSingleAccount(event, accounts);
        }
        event.reply("Die angefragten Informationen wurden in " + event.getJDA().getTextChannelById(settingsRepository.findSettingsById(1L).getHelperSpamChannelId()).getName() + " gesendet").setEphemeral(true).queue();
    }

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        if (event.getComponentId().equals("Bestätige Vergabe")) {
            Accounts accounts = accountsRepository.findAccountsByLeaderReminderMessageId(event.getInteraction().getMessageIdLong());
            if (accounts != null) {
                accounts.setLeaderReminderMessageId(0L);
                accounts.setHasReservedSlot(true);
                accountsRepository.save(accounts);
            } else {
                event.reply("Die Vergabe wurde schon bestätigt.").setEphemeral(true).queue();
            }
            event.getMessage().delete().queue();
        } else if (event.getComponentId().equals("Bestätige Entfernung")) {
            Accounts accounts = accountsRepository.findAccountsByLeaderReminderMessageId(event.getInteraction().getMessageIdLong());
            if (accounts != null) {
                accounts.setLeaderReminderMessageId(0L);
                accounts.setHasReservedSlot(false);
                accountsRepository.save(accounts);
            } else {
                event.reply("Die Entfernung wurde schon bestätigt.").setEphemeral(true).queue();
            }
            event.getMessage().delete().queue();
        } else if (event.getComponentId().equals("Mitgliedschaftsinfos")) {
            event.deferEdit().queue();

            event.getUser().openPrivateChannel()
                    .flatMap(channel -> channel.sendMessageEmbeds(embeds.createEmbedMembership().build()))
                    .queue(null, new ErrorHandler()
                            .ignore(ErrorResponse.UNKNOWN_MESSAGE)
                            .handle(
                                    ErrorResponse.CANNOT_SEND_TO_USER,
                                    (e) -> event.reply("Deine Privatsphäreeinstellungen verhindern, dass ich dir Direktnachrichten senden kann. \n \n " +
                                            "Bitte ändere das: Klicke oben links auf den Servernamen --> Klicke auf Privatsphäreeinstellungen --> Klicke auf Direktnachrichten").setEphemeral(true).queue()));
        }
    }

    @Override
    public void onGuildReady(@NotNull GuildReadyEvent event) {

        //Server-Only
        List<CommandData> commandData = new ArrayList<>();

        commandData.add(Commands.slash("admin-zahlung-hinzufügen", "Fügt dem Benutzerkonto eine neue Zahlung hinzu").setDefaultPermissions(DefaultMemberPermissions.DISABLED)
                .addOptions(
                        new OptionData(OptionType.USER, "benutzer", "Der Nutzer, der die Zahlung geleistet hat", true),
                        new OptionData(OptionType.STRING, "zahlungsdatum", "Das Datum, an dem der Nutzer die Zahlung geleistet hat. Format: tt.MM.jjjj", true),
                        new OptionData(OptionType.NUMBER, "betrag", "Der Geldbetrag, den der Nutzer bezahlt hat. Format: xxx,xx", true).setRequiredRange(0.01, 9007199254740991.000000)));

        commandData.add(Commands.slash("admin-bekomme-benutzerdaten", "Sendet alle Benutzerdaten je nach gewählten Optionen").setDefaultPermissions(DefaultMemberPermissions.DISABLED)
                .addOptions(
                        new OptionData(OptionType.USER, "benutzer", "Der Benutzer, von dem du die Informationen erhalten möchtest", false),
                        new OptionData(OptionType.INTEGER, "minimalanzahl-tage-verbleibend", "Die minimale Anzahl an Tagen, die die Mitgliedschaft noch läuft", false),
                        new OptionData(OptionType.INTEGER, "maximalanzahl-tage-verbleibend", "Die maximale Anzahl an Tagen, die die Mitgliedschaft noch läuft", false)));

        commandData.add(Commands.slash("admin-zeige-einstellungen", "Sendet alle aktuellen Einstellungen").setDefaultPermissions(DefaultMemberPermissions.DISABLED));

        commandData.add(Commands.slash("admin-ändere-einstellungen", "Ändert die aktuellen Einstellungen").setDefaultPermissions(DefaultMemberPermissions.DISABLED)
                .addOptions(
                        new OptionData(OptionType.NUMBER, "tägliche-mitgliedschaftsgebühr", "Die Mitglieschaftsgebühr, die täglich erhoben wird. Format: xxx.xx", false).setRequiredRange(0.01, 9007199254740991.000000),
                        new OptionData(OptionType.INTEGER, "erinnerung-x-tage-vor-ablauf", "Die Anzahl an Tagen, bei denen die erste Erinnerung gesendet werden soll", false),
                        new OptionData(OptionType.STRING, "spam-kanal-id", "Die ID des Textkanals in den der Bot die Antworten senden soll", false),
                        new OptionData(OptionType.STRING, "benachrichtigungskanal-id", "Die ID des Textkanals in den der Bot Benachrichtigungen senden soll", false)));

        commandData.add(Commands.slash("admin-schreibe-laufzeit-gut", "Schreibt einem Benutzer eine bestimmte Laufzeit gut").setDefaultPermissions(DefaultMemberPermissions.DISABLED)
                .addOptions(
                        new OptionData(OptionType.USER, "benutzer", "Der Nutzern dem die Laufzeit gutgeschrieben werde soll", true),
                        new OptionData(OptionType.INTEGER, "laufzeit-in-monate", "Die Laufzeit, die gutgeschrieben werden soll in Monate", true)));

        commandData.add(Commands.slash("verschenke-mitgliedschaft", "Schenke einem Nutzer einen Teil deiner Mitgliedschaftslaufzeit.")
                .addOptions(
                        new OptionData(OptionType.USER, "benutzer", "Der Nutzern, dem du einen Teil deiner Mitgliedschaftslaufzeit schenken möchtest", true),
                        new OptionData(OptionType.INTEGER, "laufzeit-in-monate", "Die Laufzeit, die übertragen werden soll in Monate", true)));

        commandData.add(Commands.slash("recruiter-erstelle-erinnerung", "Erstelle eine Erinnerung für das Recruitment").setDefaultPermissions(DefaultMemberPermissions.DISABLED)
                .addOptions(
                        new OptionData(OptionType.USER, "benutzer", "Der Nutzern, von dem eine Erinnerung erstellt werden soll für das Recruitment", true),
                        new OptionData(OptionType.STRING, "enddatum", "Das Datum, an dem die Erinnerung gesendet werden soll. Format: tt.MM.jjjj", true),
                        new OptionData(OptionType.STRING, "status", "Der Status, den der Nutzer hat", true)
                                .addChoice("Anwärter", "Anwärter")
                                .addChoice("Probezeit", "Probezeit")));

        event.getGuild().updateCommands().addCommands(commandData).queue();
    }
}
