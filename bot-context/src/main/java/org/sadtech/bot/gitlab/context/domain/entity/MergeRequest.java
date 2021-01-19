package org.sadtech.bot.gitlab.context.domain.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.sadtech.bot.gitlab.context.domain.MergeRequestState;
import org.sadtech.haiti.context.domain.BasicEntity;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Сущность ПуллРеквест.
 *
 * @author upagge [31.01.2020]
 */
@Getter
@Setter
@Entity
@Table(name = "merge_request")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class MergeRequest implements BasicEntity<Long> {

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

    @Column(name = "description")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "state")
    private MergeRequestState state;

    @Column(name = "created_date")
    private LocalDateTime createdDate;

    @Column(name = "updated_date")
    private LocalDateTime updatedDate;

    @Column(name = "web_url")
    private String webUrl;

    @Column(name = "conflict")
    private boolean conflict;

    @ManyToOne
    @JoinColumn(name = "author_id")
    private Person author;

    @ManyToOne
    @JoinColumn(name = "assignee_id")
    private Person assignee;

    @Column(name = "target_branch")
    private String targetBranch;

    @Column(name = "source_branch")
    private String sourceBranch;

    @Column(name = "notification")
    private Boolean notification;

    @ElementCollection
    @CollectionTable(name = "merge_request_label", joinColumns = @JoinColumn(name = "merge_request_id"))
    @Column(name = "label")
    private Set<String> labels = new HashSet<>();

    @Column(name = "date_last_commit")
    private LocalDateTime dateLastCommit;

//    @JoinTable
//    @OneToMany(fetch = FetchType.LAZY)
//    private List<Note> notes = new ArrayList<>();

}
