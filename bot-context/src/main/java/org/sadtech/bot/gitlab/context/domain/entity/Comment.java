package org.sadtech.bot.gitlab.context.domain.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.sadtech.haiti.context.domain.BasicEntity;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
//@Entity
//@Table(name = "comment")
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
public class Comment extends BasicEntity<Long> {

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

    @Override
    @Id
    @Column(name = "id")
    @EqualsAndHashCode.Include
    public Long getId() {
        return super.getId();
    }

    @Override
    public void setId(Long id) {
        super.setId(id);
    }
}
