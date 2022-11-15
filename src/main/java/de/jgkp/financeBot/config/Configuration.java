package de.jgkp.financeBot.config;

import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;

@Getter
@Setter
@org.springframework.context.annotation.Configuration
@PropertySource("classpath:application-prod.properties")
@ConfigurationProperties(prefix ="financebot")
public class Configuration {

    private String botToken;
    private String guildId;
}
