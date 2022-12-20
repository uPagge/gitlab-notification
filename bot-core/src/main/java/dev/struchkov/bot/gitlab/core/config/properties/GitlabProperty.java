package dev.struchkov.bot.gitlab.core.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Данные необходимые для взаимодействия с API GitLab.
 *
 * @author upagge [31.01.2020]
 */
@Getter
@Setter
@Component
@ConfigurationProperties("gitlab-bot.gitlab")
public class GitlabProperty {

    private String baseUrl;

    /**
     * Адрес, по которому можно получить открытые ПР
     */
    private String urlMergeRequestOpen;

    /**
     * Адрес, по которому можно получить закрытые ПР
     */
    private String urlMergeRequestClose;

    /**
     * Адрес, по которому можно получить комментарии к ПР
     */
    private String urlMergeRequestComment;

    /**
     * Адрес ПР
     */
    private String urlMergeRequest;

    private String urlProject;

    private String userUrl;

    private String usersUrl;

    private String urlMergeRequestAdd;

    private String urlNote;

    private String urlNoteApi;

    private String urlPipelines;

    private String urlPipeline;

    private String urlCommit;

    private String urlNewNote;

    private String urlDiscussion;

    private String urlOneDiscussion;

    private String replaceUrl;

}
