package org.sadtech.bot.vsc.bitbucketbot.context.domain.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "comment")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Comment {

    /**
     * Идентификатор
     */
    @Id
    @Column(name = "id")
    @EqualsAndHashCode.Include
    private Long id;

    @Column(name = "url_api")
    private String urlApi;

    @Column(name = "url")
    private String url;

    @Column(name = "pull_request_id")
    private Long pullRequestId;

    @Column(name = "author_login")
    private String author;

    @Column(name = "responsible_login")
    private String responsible;

    @Column(name = "message")
    private String message;

    @Column(name = "create_date")
    private LocalDateTime createDate;

    /**
     * Версия объекта в битбакет
     */
    @Column(name = "bitbucket_version")
    private Integer bitbucketVersion;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "comment_tree", joinColumns = @JoinColumn(name = "parent_id"))
    @Column(name = "child_id")
    private Set<Long> answers;

}
