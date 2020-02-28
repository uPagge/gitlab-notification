package com.tsc.bitbucketbot.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "bitbucketbot.server-send")
public class PushMessageConfig {

    private String url;

}
