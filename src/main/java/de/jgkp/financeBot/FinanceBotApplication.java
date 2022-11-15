package de.jgkp.financeBot;

import de.jgkp.financeBot.discord.Bot;
import de.jgkp.financeBot.service.StartService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ClassPathXmlApplicationContext;

@Configuration
@ConfigurationPropertiesScan
@ComponentScan
@EnableAutoConfiguration(exclude = {WebMvcAutoConfiguration.class})
public class FinanceBotApplication {

	private static StartService startService;

	public FinanceBotApplication(StartService startService) {
		FinanceBotApplication.startService = startService;
	}

	public static void main(String[] args) throws InterruptedException {
		SpringApplication.run(FinanceBotApplication.class, args);
		startService.start();
	}

}
