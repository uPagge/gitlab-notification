package org.sadtech.bot.bitbucketbot.domain.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.sadtech.bot.bitbucketbot.domain.TaskStatus;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "task")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Task {

    /**
     * Идентификатор
     */
    @Id
    @Column(name = "id")
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    @JoinTable
    @OneToMany(cascade = CascadeType.ALL)
    private List<Comment> comments;

}
