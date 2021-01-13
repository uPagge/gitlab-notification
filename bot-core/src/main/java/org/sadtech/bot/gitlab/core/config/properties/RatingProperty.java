package org.sadtech.bot.gitlab.core.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * // TODO: 25.10.2020 Добавить описание.
 *
 * @author upagge 25.10.2020
 */
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "bitbucketbot.rating")
public class RatingProperty {

    boolean enabled = false;

}
