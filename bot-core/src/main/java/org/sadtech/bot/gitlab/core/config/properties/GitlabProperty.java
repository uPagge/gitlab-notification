package org.sadtech.bot.gitlab.core.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Данные необходимые для взаимодействия с API Bitbucket.
 *
 * @author upagge [31.01.2020]
 */
@Getter
@Setter
@Component
@ConfigurationProperties("gitlab-bot.gitlab")
public class GitlabProperty {

    /**
     * Адрес, по которому можно получить открытые ПР
     */
    private String urlPullRequestOpen;

    /**
     * Адрес, по которому можно получить закрытые ПР
     */
    private String urlPullRequestClose;

    /**
     * Адрес, по которому можно получить комментарии к ПР
     */
    private String urlPullRequestComment;

    /**
     * Адрес ПР
     */
    private String urlPullRequest;

    private String urlProject;

    private String userUrl;

    private String usersUrl;

    private String urlMergeRequestAdd;

    private String urlNote;

}
