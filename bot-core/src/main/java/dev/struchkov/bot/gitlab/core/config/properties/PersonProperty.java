package dev.struchkov.bot.gitlab.core.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author upagge 15.01.2021
 */

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "gitlab-bot.person")
public class PersonProperty {

    private String token;
    private Long telegramId;

}
