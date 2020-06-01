package org.sadtech.bot.bitbucketbot.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "bitbucketbot.init")
public class InitConfig {

    private Long startCommentId;

}
