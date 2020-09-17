package org.sadtech.bot.vcs.core.domain.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.sadtech.bot.vcs.core.domain.PullRequestStatus;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Сущность ПуллРеквест.
 *
 * @author upagge [31.01.2020]
 */
@Getter
@Setter
@Entity
@Table(name = "pull_request")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class PullRequest {

    /**
     * Идентификатор
     */
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    /**
     * Идентификатор на стороне битбакета
     */
    @Column(name = "bitbucket_id")
    private Long bitbucketId;

    /**
     * Идентификатор репозитория на стороне битбакета
     */
    @Column(name = "repository_id")
    private Long repositoryId;

    /**
     * Идентификатор проекта на стороне битбакета
     */
    @Column(name = "project_key")
    private String projectKey;

    /**
     * Символьный идентификатор на стороне битбакета
     */
    @Column(name = "repository_slug")
    private String repositorySlug;

    /**
     * Описание пулреквеста
     */
    @Column(name = "description")
    private String description;

    /**
     * Адрес ПР
     */
    @Column(name = "url")
    private String url;

    /**
     * Название ПР
     */
    @Column(name = "title")
    private String title;

    /**
     * Статус ПР
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private PullRequestStatus status;

    /**
     * Дата создания
     */
    @Column(name = "create_date")
    private LocalDateTime createDate;

    /**
     * Дата обновления
     */
    @Column(name = "update_date")
    private LocalDateTime updateDate;

    /**
     * Флаг показывающий наличие конфликта в ПР
     */
    @Column(name = "conflict")
    private boolean conflict;

    /**
     * Версия объекта в битбакет
     */
    @Column(name = "bitbucket_version")
    private Integer bitbucketVersion;

    /**
     * Автор ПР
     */
    @Column(name = "author_login")
    private String authorLogin;

    /**
     * Ревьюверы
     */
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true, mappedBy = "pullRequest")
    private List<Reviewer> reviewers = new ArrayList<>();

    public void setReviewers(List<Reviewer> reviewers) {
        reviewers.forEach(reviewer -> reviewer.setPullRequest(this));
        this.reviewers = reviewers;
    }

}
