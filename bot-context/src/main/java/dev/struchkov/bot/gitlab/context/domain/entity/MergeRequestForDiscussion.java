package dev.struchkov.bot.gitlab.context.domain.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * @author upagge 12.09.2020
 */
@Getter
@Setter
@Entity
@Table(name = "merge_request")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class MergeRequestForDiscussion {

    /**
     * Идентификатор
     */
    @Id
    @Column(name = "id")
    @EqualsAndHashCode.Include
    private Long id;

    @Column(name = "two_id")
    private Long twoId;

    @Column(name = "project_id")
    private Long projectId;

    @Column(name = "title")
    private String title;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "author_id")
    private Person author;

    @Column(name = "web_url")
    private String webUrl;

}
