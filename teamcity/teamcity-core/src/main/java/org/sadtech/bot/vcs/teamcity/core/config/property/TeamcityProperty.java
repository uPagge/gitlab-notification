package org.sadtech.bot.vcs.teamcity.core.config.property;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * // TODO: 21.09.2020 Добавить описание.
 *
 * @author upagge 21.09.2020
 */
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "bitbucketbot.teamcity")
public class TeamcityProperty {

    private String token;
    private String projectUrl;
    private String buildUrl;

}
