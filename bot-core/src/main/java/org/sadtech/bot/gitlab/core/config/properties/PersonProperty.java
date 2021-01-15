package org.sadtech.bot.gitlab.core.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * // TODO: 15.01.2021 Добавить описание.
 *
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
