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

    private String replaceUrl;

    private String usersUrl;

    private String userUrl;

    private String projectsUrl;

    /**
     * Адрес, по которому можно получить открытые MR
     */
    private String openMergeRequestsUrl;

    /**
     * Адрес, по которому можно получить закрытые MR
     */
    private String closeMergeRequestsUrl;

    /**
     * Адрес, по которому можно получить комментарии к MR
     */
    private String commentsOfMergeRequestUrl;

    /**
     * Адрес MR
     */
    private String mergeRequestUrl;

    private String projectAddUrl;

    private String noteUrl;

    private String notesOfMergeRequestUrl;

    private String pipelinesUrl;

    private String pipelineUrl;

    private String lastCommitOfMergeRequestUrl;

    private String newNoteUrl;

    /**
     * Адрес дискуссий для MR
     */
    private String discussionsUrl;

    private String discussionUrl;


}
