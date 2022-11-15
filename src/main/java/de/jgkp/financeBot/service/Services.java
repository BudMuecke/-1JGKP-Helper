package de.jgkp.financeBot.service;

import de.jgkp.financeBot.db.entities.Accounts;
import org.joda.time.LocalDate;
import org.joda.time.Period;
import org.joda.time.PeriodType;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@Service
public class Services {

    public LocalDate getCurrentDate() {
        return new LocalDate();
    }

    public int calcDayDifference(String paymentDate) throws ParseException {

        LocalDate currentDate = new LocalDate();

        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        Date date = sdf.parse(paymentDate);
        sdf.applyPattern("yyyy-MM-dd");
        paymentDate = sdf.format(date);

        LocalDate endDate = LocalDate.parse(paymentDate);

        Period p = new Period(endDate, currentDate, PeriodType.days());

        int daysDifference = p.getDays();

        if (daysDifference >= 0) {
            return daysDifference;
        } else return -1;
    }

    public String calcEndDate(int newDaysLeft) {
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        Date currentDate = new Date();

        Calendar c = Calendar.getInstance();
        c.setTime(currentDate);

        c.add(Calendar.DATE, newDaysLeft);

        Date currentDateNew = c.getTime();

        return dateFormat.format(currentDateNew);
    }
}
