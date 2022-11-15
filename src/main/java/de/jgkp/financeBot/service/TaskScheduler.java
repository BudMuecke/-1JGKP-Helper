package de.jgkp.financeBot.service;

import de.jgkp.financeBot.config.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@Service
public class TaskScheduler {

    private Configuration configuration;
    private AccountupdatesService accountupdatesService;
    private Services services;
    private DailyTask dailyTask;

    @Autowired
    public TaskScheduler(Configuration configuration, AccountupdatesService accountupdatesService, Services services, DailyTask dailyTask) {
        this.configuration = configuration;
        this.accountupdatesService = accountupdatesService;
        this.services = services;
        this.dailyTask = dailyTask;
    }

    private final ScheduledExecutorService scheduler = Executors
            .newScheduledThreadPool(1);

    public void startScheduleTask() {

        final ScheduledFuture<?> taskHandle = scheduler.scheduleWithFixedDelay(
                () -> {
                    try {

                        if (!Objects.equals(String.valueOf(services.getCurrentDate()), accountupdatesService.getCurrentDatabaseDate())){
                            dailyTask.updateAccounts();
                            accountupdatesService.setCurrentDatabaseDate();
                            System.out.println("updates completed");
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }, 0, 1, TimeUnit.DAYS);
    }
}
