package org.sadtech.bot.gitlab.core.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * // TODO: 11.10.2020 Добавить описание.
 *
 * @author upagge 11.10.2020
 */
@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "gitlab-bot")
public class AppProperty {

    private String version;

}
