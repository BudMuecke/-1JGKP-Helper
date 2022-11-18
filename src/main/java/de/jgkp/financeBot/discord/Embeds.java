package de.jgkp.financeBot.discord;

import de.jgkp.financeBot.db.repositories.SettingsRepository;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.util.Locale;

@Component
public class Embeds {

    private SettingsRepository settingsRepository;

    @Autowired
    public Embeds(SettingsRepository settingsRepository) {
        this.settingsRepository = settingsRepository;
    }

    public EmbedBuilder createEmbedExpiredReminder(String username) {

        EmbedBuilder eb = new EmbedBuilder();

        eb.setTitle("Deine 1JGKP Mitgliedschaft ist ausgelaufen", null);

        eb.setColor(new Color(0x3D302B));

        eb.setDescription("Du hast alle Vorteile der Mitgliedschaft verloren, wie z.B. den erweiterten reserved Slot für den Squad Server DSG! \n \n" +
                "Wenn du Informationen darüber wünschst, wie du deine Mitgliedschaft erneuern kannst, gib bitte /mitgliedschaftsinfos ein oder klicke auf den Button unten.");

        eb.setAuthor("Attention, "+ username +"!", null);

        eb.setFooter("© official 1JGKP Bot", "https://avatars.akamai.steamstatic.com/e2a1030b2bfe144682d465045c05a31f2baabfcf_full.jpg");

        eb.setThumbnail("https://deutsche-squad-gemeinschaft.s3.eu-central-1.amazonaws.com/public/clanlogos/CbEyII5X3dFoly7XING8vXauQKgkBK0mfYhNIlZa.png");
        return eb;
    }

    public EmbedBuilder createEmbedXDaysLeftReminder(String username) {

        EmbedBuilder eb = new EmbedBuilder();

        eb.setTitle("Deine 1JGKP Mitgliedschaft endet in "+ settingsRepository.findSettingsById(1L).getMembershipExpiresInXDaysReminderDaysAmount() +" Tagen", null);

        eb.setColor(new Color(0x3D302B));

        eb.setDescription("Wenn die Mitgliedschaft endet, verlierst du alle Vorteile aus der Mitgliedschaft, wie z.B. den erweiterten reserved Slot für den Squad Server DSG! \n \n" +
                "Wenn du Informationen darüber wünschst, wie du deine Mitgliedschaft verlängern kannst, gib bitte /mitgliedschaftsinfos ein oder klicke auf den Button unten.");

        eb.setAuthor("Aufgepasst, "+ username +"!", null);

        eb.setFooter("© official 1JGKP Bot", "https://avatars.akamai.steamstatic.com/e2a1030b2bfe144682d465045c05a31f2baabfcf_full.jpg");

        eb.setThumbnail("https://deutsche-squad-gemeinschaft.s3.eu-central-1.amazonaws.com/public/clanlogos/CbEyII5X3dFoly7XING8vXauQKgkBK0mfYhNIlZa.png");
        return eb;
    }

    public EmbedBuilder createEmbedMyMembership(String username, String lastPayment, String amount, String daysLeft, String finalHasReservedSlot, String enddate) {

        String NewAmount = String.valueOf(amount).replace(".", ",");

        EmbedBuilder eb = new EmbedBuilder();

        eb.setTitle("Deine 1JGKP Mitgliedschaftsinformationen", null);

        eb.setColor(new Color(0x3D302B));

        eb.addField(":date: Letzte Zahlung:", lastPayment, true);
        eb.addField(":money_with_wings: Betrag letzter Zahlung:", NewAmount + "€", true);
        eb.addBlankField(true);
        eb.addField(":alarm_clock: Mitgliedschaft endet in:", daysLeft + " Tagen", true);
        eb.addField(":date: Mitgliedschaft endet am:", enddate, true);
        eb.addBlankField(true);
        eb.addField(":question: Erweiterter Reserved Slot aktiviert: "+ finalHasReservedSlot,"", true);

        eb.setAuthor("Informationen für "+ username, null);

        eb.setFooter("© official 1JGKP Bot", "https://avatars.akamai.steamstatic.com/e2a1030b2bfe144682d465045c05a31f2baabfcf_full.jpg");

        eb.setThumbnail("https://deutsche-squad-gemeinschaft.s3.eu-central-1.amazonaws.com/public/clanlogos/CbEyII5X3dFoly7XING8vXauQKgkBK0mfYhNIlZa.png");
        return eb;
    }
    public EmbedBuilder createEmbedNoRegisteredPayments(String username) {

        EmbedBuilder eb = new EmbedBuilder();

        eb.setTitle("Du hast keine registrierten Mitgliedschaftszahlungen", null);

        eb.setColor(new Color(0x3D302B));

        eb.setDescription("Wir können dir keine Informationen geben. \n\n" +
                "Wenn du in der Vergangenheit eine Mitgliedszahlung geleistet hast und glaubst, dass diese Anzeige ein Fehler ist, wende dich bitte an einen der 1JGKP-Leiter!\n\n" +
                "Für Informationen über die Mitgliedschaft im Allgemeinen, gib bitte /mitgliedschaftsinfos ein oder klicke auf den Button unten.");

        eb.setAuthor("Sorry, "+ username +"!", null);

        eb.setFooter("© official 1JGKP Bot", "https://avatars.akamai.steamstatic.com/e2a1030b2bfe144682d465045c05a31f2baabfcf_full.jpg");

        eb.setThumbnail("https://deutsche-squad-gemeinschaft.s3.eu-central-1.amazonaws.com/public/clanlogos/CbEyII5X3dFoly7XING8vXauQKgkBK0mfYhNIlZa.png");
        return eb;
    }

    public EmbedBuilder createEmbedRemindLeader(String username) {

        EmbedBuilder eb = new EmbedBuilder();

        eb.setTitle("Die Mitgliedschaft von "+ username + " ist ausgelaufen", null);

        eb.setColor(new Color(0x3D302B));

        eb.setDescription( "Bitte entferne den erweiterten reserved Slot des Benutzers über die DSG-Website!\n\n" +
                "Klicke auf den Button unten, nachdem die Entfernung durchgeführt wurde, um zu bestätigen, dass der Benutzer keinen erweiterten reserved Slot mehr hat");


        eb.setFooter("© official 1JGKP Bot", "https://avatars.akamai.steamstatic.com/e2a1030b2bfe144682d465045c05a31f2baabfcf_full.jpg");

        eb.setThumbnail("https://deutsche-squad-gemeinschaft.s3.eu-central-1.amazonaws.com/public/clanlogos/CbEyII5X3dFoly7XING8vXauQKgkBK0mfYhNIlZa.png");
        return eb;
    }

    public EmbedBuilder createEmbedConfirmMessage(String username) {

        EmbedBuilder eb = new EmbedBuilder();

        eb.setTitle("Die Mitgliedschaft von "+ username + " ist gestartet", null);

        eb.setColor(new Color(0x3D302B));

        eb.setDescription("Bitte füge den erweiterten reserved Slot des Benutzers über die DSG-Website hinzu!\n\n" +
                "Klicke nach dem Hinzufügen auf den Button unten, um zu bestätigen, dass der Benutzer seinen erweiterten reserved Slot erhalten hat");


        eb.setFooter("© official 1JGKP Bot", "https://avatars.akamai.steamstatic.com/e2a1030b2bfe144682d465045c05a31f2baabfcf_full.jpg");

        eb.setThumbnail("https://deutsche-squad-gemeinschaft.s3.eu-central-1.amazonaws.com/public/clanlogos/CbEyII5X3dFoly7XING8vXauQKgkBK0mfYhNIlZa.png");
        return eb;
    }

    public EmbedBuilder createEmbedConfirmedPayment(String username, String amount, String date, String reviser) {

        String NewAmount = String.valueOf(amount).replace(".", ",");

        EmbedBuilder eb = new EmbedBuilder();

        eb.setTitle("Zahlungsinformationen von " + username, null);

        eb.setColor(new Color(0x3D302B));

        eb.addField(":bust_in_silhouette: Name:", username, true);
        eb.addField(":date: Zahlungsdatum:", date, true);
        eb.addBlankField(true);
        eb.addField(":money_with_wings: Betrag:", NewAmount + "€", true);
        eb.addField(":pencil: Bearbeiter:", reviser, true);
        eb.addBlankField(true);


        eb.setFooter("© official 1JGKP Bot", "https://avatars.akamai.steamstatic.com/e2a1030b2bfe144682d465045c05a31f2baabfcf_full.jpg");

        eb.setThumbnail("https://deutsche-squad-gemeinschaft.s3.eu-central-1.amazonaws.com/public/clanlogos/CbEyII5X3dFoly7XING8vXauQKgkBK0mfYhNIlZa.png");
        return eb;
    }

    public EmbedBuilder createEmbedHelpMessage(String username) {

        EmbedBuilder eb = new EmbedBuilder();

        eb.setTitle("Ich bin hier, um dir zu helfen, "+ username + "!", null);

        eb.setColor(new Color(0x3D302B));

        eb.setDescription("Klicke auf den Button unten, um die 1JGKP Helper Bot Hilfsdokumentation zu öffnen");


        eb.setFooter("© official 1JGKP Bot", "https://avatars.akamai.steamstatic.com/e2a1030b2bfe144682d465045c05a31f2baabfcf_full.jpg");

        eb.setThumbnail("https://deutsche-squad-gemeinschaft.s3.eu-central-1.amazonaws.com/public/clanlogos/CbEyII5X3dFoly7XING8vXauQKgkBK0mfYhNIlZa.png");
        return eb;
    }

    public EmbedBuilder createEmbedMembership() {

        EmbedBuilder eb = new EmbedBuilder();

        eb.setTitle("Allgemeine Mitgliedschaftsinformationen", null);

        eb.setColor(new Color(0x3D302B));

        double dailyMembershipFee = settingsRepository.findSettingsById(1L).getDailyMembershipFee();
        double yearlyMembershipFee = 365 * dailyMembershipFee;
        double halfyearlyMembershipFee = 365 * dailyMembershipFee / 2;
        double monthlyMembershipFee = 365 * dailyMembershipFee / 12;

        yearlyMembershipFee = Math.round(100.0 * yearlyMembershipFee) / 100.0;
        halfyearlyMembershipFee = Math.round(100.0 * halfyearlyMembershipFee) / 100.0;
        monthlyMembershipFee = Math.round(100.0 * monthlyMembershipFee) / 100.0;

        String newYearlyMembershipFee = String.format(Locale.GERMAN,"%,.2f", yearlyMembershipFee);
        String newHalfyearlyMembershipFee = String.format(Locale.GERMAN,"%,.2f", halfyearlyMembershipFee);
        String newMonthlyMembershipFee = String.format(Locale.GERMAN,"%,.2f", monthlyMembershipFee);

        eb.setDescription("**Wir bieten die 1JGKP Mitgliedschaft aktuell nur Anwärtern, Probezeitlern und Mitgliedern der 1JGKP an!** \n \n " +
                "Die 1JGKP Mitgliedschaft ist ein Weg, die 1JGKP finanziell zu unterstützen, denn wir investieren aktiv in den Clan, zahlen Serverkosten für einen Squad Server, " +
                "führen Projekte und Events mit Preisen durch und haben noch weitere Kosten, die du gerne im Transparency Report nachlesen kannst, der einmal im Quartal veröffentlicht wird. Das gesamte Geld kommt dem Clan zugute!" +
                "\n \n **Deine Vorteile, wenn du uns finanziell unterstützt:** \n \n " +
                "- erweiterter Reserved Slot auf dem DSG Squad Server \n \n" +
                "- ein exklusives Achievement auf dem 1JGKP Discord \n \n" +
                "- jährlich zahlende Mitglieder bekommen bei der 1JGKP Weihnachtsaktion 2 Lose GRATIS! \n \n" +
                "- halbjährlich zahlende Mitglieder bekommen bei der 1JGKP Weihnachtsaktion 1 Los GRATIS! \n \n "+
                "**Du willst uns jetzt finanziell unterstützen?** \n \n" +
                "Sende uns deinen Beitrag über PayPal und schreibe deinen Discord Namen dazu: \n \n" +
                "**1JGKP-Leitung@web.de** \n \n" +
                "Du hast kein PayPal? Kein Problem! \n Bitte kontaktiere PinkFluffyUnicorn_Lukas#2984 \n \n" +
                "**Dein gesendeter Beitrag wird in eine Mitgliedschaftslaufzeit umgewandelt. Du kannst dadurch selbst entscheiden wie lange du die Vorteile beanspruchen möchtest!** \n \n" +
                "1 Jahr Laufzeit entspricht " + newYearlyMembershipFee + "€\n" +
                "1 Halbjahr Laufzeit entspricht " + newHalfyearlyMembershipFee + "€\n" +
                "1 Monat Laufzeit entspricht " + newMonthlyMembershipFee + "€\n \n" +
                "**Die Mindestlaufzeit beträgt 1 Monat!** \n \n" +
                "Bitte beachte, dass sich durch eventuelle zukünftige Anpassungen der Mitgliedsbeitragshöhen auch laufende Laufzeiten entsprechend anpassen könnten. \n \n" +
                "**Ich möchte meine Mitgliedschaft verlängern** \n \n" +
                "Bei einer Verlängerung gelten die oben aufgeführten Voraussetzungen, Vorteile und Laufzeiten. Bitte beachte: "+
                "Deine noch verbleibende Mitgliedschaft wird mit der Verlängerten verrechnet. \n \n" +
                "Nach Eingang der Zahlung kann die Bearbeitung mehrere Tage dauern. Für diese Zeit ziehen wir dir natürlich keine Mitgliedschaftslaufzeit ab! \n \n " +
                "Bei Fragen, wende dich bitte an ein 1JGKP Leitungsmitglied! \n \n" +
                "Danke für deine Unterstützung! :)"
        );

        eb.setFooter("© official 1JGKP Bot", "https://avatars.akamai.steamstatic.com/e2a1030b2bfe144682d465045c05a31f2baabfcf_full.jpg");

        eb.setThumbnail("https://deutsche-squad-gemeinschaft.s3.eu-central-1.amazonaws.com/public/clanlogos/CbEyII5X3dFoly7XING8vXauQKgkBK0mfYhNIlZa.png");
        return eb;
    }

    public EmbedBuilder createEmbedSettings(double dailyMembershipFee, int membershipExpiresReminderDays, String notificationChannelId, String spamChannelId, String recruitmentChannelId, String notificationChannelName, String spamChannelName, String recruitmentChannelName) {

        EmbedBuilder eb = new EmbedBuilder();

        eb.setTitle("Derzeitige Einstellungen", null);

        eb.setColor(new Color(0x3D302B));

        eb.addField(":money_with_wings: Tägliche Mitgliedschaftsgebühr:", String.valueOf(dailyMembershipFee), false);
        eb.addField(":date: Erinnerung X Tage vor Ablauf:", String.valueOf(membershipExpiresReminderDays), false);
        eb.addBlankField(true);
        eb.addField(":pencil: Spam-Kanal-Name:", spamChannelName, false);
        eb.addField(":pencil: Spam-Kanal-ID:", spamChannelId, false);
        eb.addBlankField(true);
        eb.addField(":mega: Benachrichtigungskanal-Name:", notificationChannelName, false);
        eb.addField(":mega: Benachrichtigungskanal-ID:", notificationChannelId, false);
        eb.addBlankField(true);
        eb.addField(":briefcase: Recruitmentkanal-Name:", recruitmentChannelName, false);
        eb.addField(":briefcase: Recruitmentkanal-ID:", recruitmentChannelId, false);

        eb.setFooter("© official 1JGKP Bot", "https://avatars.akamai.steamstatic.com/e2a1030b2bfe144682d465045c05a31f2baabfcf_full.jpg");

        eb.setThumbnail("https://deutsche-squad-gemeinschaft.s3.eu-central-1.amazonaws.com/public/clanlogos/CbEyII5X3dFoly7XING8vXauQKgkBK0mfYhNIlZa.png");
        return eb;
    }

    public EmbedBuilder createEmbedGiftRuntime(String donor, String giftedOne, int runtime) {

        EmbedBuilder eb = new EmbedBuilder();

        eb.setTitle("Mitgliedschaft wurde verschenkt!", null);

        eb.setColor(new Color(0x3D302B));

        eb.setDescription(donor + " hat " + giftedOne + " " + runtime + " Monat(e) von seiner Mitgliedschaftslaufzeit geschenkt!");

        eb.setFooter("© official 1JGKP Bot", "https://avatars.akamai.steamstatic.com/e2a1030b2bfe144682d465045c05a31f2baabfcf_full.jpg");

        eb.setThumbnail("https://deutsche-squad-gemeinschaft.s3.eu-central-1.amazonaws.com/public/clanlogos/CbEyII5X3dFoly7XING8vXauQKgkBK0mfYhNIlZa.png");
        return eb;
    }

    public EmbedBuilder createEmbedAddRuntime(String reviser, String recipient, int runtime) {

        EmbedBuilder eb = new EmbedBuilder();

        eb.setTitle("Mitgliedschaft wurde hinzugefügt!", null);

        eb.setColor(new Color(0x3D302B));

        eb.setDescription(reviser + " hat " + recipient + " " + runtime + " Monat(e) Mitgliedschaftslaufzeit hinzugefügt!");

        eb.setFooter("© official 1JGKP Bot", "https://avatars.akamai.steamstatic.com/e2a1030b2bfe144682d465045c05a31f2baabfcf_full.jpg");

        eb.setThumbnail("https://deutsche-squad-gemeinschaft.s3.eu-central-1.amazonaws.com/public/clanlogos/CbEyII5X3dFoly7XING8vXauQKgkBK0mfYhNIlZa.png");
        return eb;
    }

    public EmbedBuilder createEmbedCantWriteUser(String userName, String reason) {

        EmbedBuilder eb = new EmbedBuilder();

        eb.setTitle("Nachricht an " + userName +" konnte nicht gesendet werden", null);

        eb.setColor(new Color(0x3D302B));

        eb.setDescription("Dieser Fehler tritt auf, da der Empfänger keine Direktnachrichten von nicht befreundeten Usern zulässt.\n \n" +
                "Nachrichtentyp: " + reason);

        eb.setFooter("© official 1JGKP Bot", "https://avatars.akamai.steamstatic.com/e2a1030b2bfe144682d465045c05a31f2baabfcf_full.jpg");

        eb.setThumbnail("https://deutsche-squad-gemeinschaft.s3.eu-central-1.amazonaws.com/public/clanlogos/CbEyII5X3dFoly7XING8vXauQKgkBK0mfYhNIlZa.png");
        return eb;
    }

    public EmbedBuilder createEmbedCandidateTimeEnded(String userName, String status) {

        EmbedBuilder eb = new EmbedBuilder();

        eb.setTitle("Recruitment Erinnerung", null);

        eb.setColor(new Color(0x3D302B));

        eb.setDescription("**Erinnerung zum Nutzer:** " + userName + "\n**Status des Nutzers:** " + status);

        eb.setFooter("© official 1JGKP Bot", "https://avatars.akamai.steamstatic.com/e2a1030b2bfe144682d465045c05a31f2baabfcf_full.jpg");

        eb.setThumbnail("https://deutsche-squad-gemeinschaft.s3.eu-central-1.amazonaws.com/public/clanlogos/CbEyII5X3dFoly7XING8vXauQKgkBK0mfYhNIlZa.png");
        return eb;
    }

    public EmbedBuilder createEmbedCandidateInfo(String userName, String status, String enddate) {

        EmbedBuilder eb = new EmbedBuilder();

        eb.setTitle("Infos zur Erinnerung zum Nutzer " + userName, null);

        eb.setColor(new Color(0x3D302B));

        eb.setDescription("**Nutzername:** " + userName + "\n**Status des Nutzers:** " + status + "\n**Erinnerung wird gesendet am:** "+ enddate);

        eb.setFooter("© official 1JGKP Bot", "https://avatars.akamai.steamstatic.com/e2a1030b2bfe144682d465045c05a31f2baabfcf_full.jpg");

        eb.setThumbnail("https://deutsche-squad-gemeinschaft.s3.eu-central-1.amazonaws.com/public/clanlogos/CbEyII5X3dFoly7XING8vXauQKgkBK0mfYhNIlZa.png");
        return eb;
    }

    public EmbedBuilder createEmbedDoneeInfo(String userName, String donatorName, int runtime) {

        EmbedBuilder eb = new EmbedBuilder();

        eb.setTitle("Hey, " + userName +"!", null);

        eb.setColor(new Color(0x3D302B));

        eb.setDescription(donatorName + " hat dir gerade " + runtime +" Monat(e) 1JGKP Mitgliedschaft spendiert!\n\n" +
                "Du genießt nun alle Vorteile wie zum Beispiel den erweiterten Reserved Slot auf dem DSG Server, wenn du noch keine Mitgliedschaft hattest. " +
                "Solltest du schon eine Mitgliedschaft besessen haben, wurde diese um den oben genannten Betrag verlängert.");

        eb.setFooter("© official 1JGKP Bot", "https://avatars.akamai.steamstatic.com/e2a1030b2bfe144682d465045c05a31f2baabfcf_full.jpg");

        eb.setThumbnail("https://deutsche-squad-gemeinschaft.s3.eu-central-1.amazonaws.com/public/clanlogos/CbEyII5X3dFoly7XING8vXauQKgkBK0mfYhNIlZa.png");
        return eb;
    }

    public EmbedBuilder createEmbedRecipientInfo(String recipientName, int runtime) {

        EmbedBuilder eb = new EmbedBuilder();

        eb.setTitle("Hey, " + recipientName +"!", null);

        eb.setColor(new Color(0x3D302B));

        eb.setDescription("Dir wurden soeben " + runtime + " Monate(e) 1JGKP Mitgliedschaft von der 1JGKP Leitung gutgeschrieben!\n\n" +
                "Du genießt nun alle Vorteile wie zum Beispiel den erweiterten Reserved Slot auf dem DSG Server, wenn du noch keine Mitgliedschaft hattest." +
                "Solltest du schon eine Mitgliedschaft besessen haben, wurde diese um den oben genannten Betrag verlängert.");

        eb.setFooter("© official 1JGKP Bot", "https://avatars.akamai.steamstatic.com/e2a1030b2bfe144682d465045c05a31f2baabfcf_full.jpg");

        eb.setThumbnail("https://deutsche-squad-gemeinschaft.s3.eu-central-1.amazonaws.com/public/clanlogos/CbEyII5X3dFoly7XING8vXauQKgkBK0mfYhNIlZa.png");
        return eb;
    }

    public EmbedBuilder createEmbedConfirmedPaymentUser(String username, String amount, String date, String reviser) {

        String NewAmount = String.valueOf(amount).replace(".", ",");

        EmbedBuilder eb = new EmbedBuilder();

        eb.setTitle("Zahlungsinformationen von " + username, null);

        eb.setColor(new Color(0x3D302B));

        eb.setDescription("Deine 1JGKP Mitgliedschaft wurde hinzugefügt oder verlängert!");

        eb.addField(":bust_in_silhouette: Name:", username, true);
        eb.addField(":date: Zahlungsdatum:", date, true);
        eb.addBlankField(true);
        eb.addField(":money_with_wings: Betrag:", NewAmount + "€", true);
        eb.addField(":pencil: Bearbeiter:", reviser, true);
        eb.addBlankField(true);


        eb.setFooter("© official 1JGKP Bot", "https://avatars.akamai.steamstatic.com/e2a1030b2bfe144682d465045c05a31f2baabfcf_full.jpg");

        eb.setThumbnail("https://deutsche-squad-gemeinschaft.s3.eu-central-1.amazonaws.com/public/clanlogos/CbEyII5X3dFoly7XING8vXauQKgkBK0mfYhNIlZa.png");
        return eb;
    }
}

