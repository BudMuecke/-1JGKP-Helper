package de.jgkp.financeBot.service;

import de.jgkp.financeBot.db.entities.Accounts;
import net.dv8tion.jda.api.JDA;
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

    public int calcDayDifferenceRecruitment(String reminderDate) throws ParseException {

        LocalDate currentDate = new LocalDate();

        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        Date date = sdf.parse(reminderDate);
        sdf.applyPattern("yyyy-MM-dd");
        reminderDate = sdf.format(date);

        LocalDate endDate = LocalDate.parse(reminderDate);

        Period p = new Period(endDate, currentDate, PeriodType.days());

        int daysDifference = p.getDays();

        if (daysDifference < 0) {
            return daysDifference;
        } else return 1;
    }

    public boolean checkIfTextchannelExists(JDA jda, String id){
        try {
            jda.getTextChannelById(id);
        }catch(Exception e){
            return false;
        }
        return true;
    }

    public boolean checkIfDoubleNotNegative(double number){
        if(number < 0){
            return false;
        }
        return true;
    }

    public boolean checkIfIntegerNotNegative(double number){
        if(number < 0){
            return false;
        }
        return true;
    }

    public boolean checkIfRuntimeGreaterThanOne(int runtime){
        if(runtime < 1){
            return false;
        }
        return true;
    }

    public boolean validateDate(String date){
        if(date.length() == 10){
          if (Integer.parseInt(date.substring(0,2)) < 32 && Integer.parseInt(date.substring(0,2)) > 0){
              if (Integer.parseInt(date.substring(3,5)) < 13 && Integer.parseInt(date.substring(3,5)) > 0){
                  if (Integer.parseInt(date.substring(6,10)) > 2020){
                      return true;
                  }
              }
          }
        }
        return false;
    }

    public String parseDate(String endDate) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        Date date = sdf.parse(endDate);
        sdf.applyPattern("yyyy-MM-dd");
        endDate = sdf.format(date);
        return endDate;
    }
}
