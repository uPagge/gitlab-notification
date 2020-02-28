package com.tsc.bitbucketbot.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * TODO: Добавить описание класса.
 *
 * @author upagge [31.01.2020]
 */
@Data
@Component
@ConfigurationProperties("bitbucketbot.bitbucket")
public class BitbucketConfig {

    private String token;
    private String urlPullRequestOpen;
    private String urlPullRequestClose;

}
