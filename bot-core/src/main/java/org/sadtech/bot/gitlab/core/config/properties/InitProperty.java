package org.sadtech.bot.gitlab.core.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "bitbucketbot.init")
public class InitProperty {

    private Long startCommentId;
    private boolean use = false;

}
