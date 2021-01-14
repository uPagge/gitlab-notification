package org.sadtech.bot.gitlab.context.domain.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.sadtech.bot.gitlab.context.domain.TaskStatus;
import org.sadtech.haiti.context.domain.BasicEntity;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

//@Entity
@Getter
@Setter
//@Table(name = "task")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Task implements BasicEntity<Long> {

    @Id
    @Column(name = "id")
    @EqualsAndHashCode.Include
    private Long id;

    /**
     * Описание задачи
     */
    @Column(name = "description")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private TaskStatus status;

    @Column(name = "create_date")
    private LocalDateTime createDate;

    @Column(name = "pull_request_id")
    private Long pullRequestId;

    @Column(name = "url")
    private String url;

    @Column(name = "url_api")
    private String urlApi;

    /**
     * Версия объекта в битбакет
     */
    @Column(name = "bitbucket_version")
    private Integer bitbucketVersion;

    @Column(name = "author_login")
    private String author;

    @Column(name = "responsible_login")
    private String responsible;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "task_comments", joinColumns = @JoinColumn(name = "task_id"))
    @Column(name = "comment_id")
    private Set<Long> answers = new HashSet<>();

}
