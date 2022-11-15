package de.jgkp.financeBot.service;

import de.jgkp.financeBot.discord.Bot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StartService {

    private TaskScheduler taskScheduler;
    private Bot bot;

    @Autowired
    public StartService(TaskScheduler taskScheduler, Bot bot){
        this.taskScheduler = taskScheduler;
        this.bot = bot;
    }

    public void start() throws InterruptedException {
        bot.startBot();
        taskScheduler.startScheduleTask();
    }
}
