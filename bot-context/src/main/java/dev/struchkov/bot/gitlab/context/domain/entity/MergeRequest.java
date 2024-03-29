package dev.struchkov.bot.gitlab.context.domain.entity;

import dev.struchkov.bot.gitlab.context.domain.MergeRequestState;
import dev.struchkov.haiti.utils.fieldconstants.annotation.FieldNames;
import dev.struchkov.haiti.utils.fieldconstants.domain.Mode;
import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Сущность ПуллРеквест.
 *
 * @author upagge [31.01.2020]
 */
@Getter
@Setter
@Entity
@FieldNames(mode = {Mode.TABLE, Mode.SIMPLE})
@Table(name = "merge_request")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class MergeRequest {

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

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "author_id")
    private Person author;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "assignee_id")
    private Person assignee;

    @OneToMany(
            fetch = FetchType.LAZY,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE}
    )
    @JoinTable(
            name = "merge_request_reviewer",
            joinColumns = @JoinColumn(name = "merge_request_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "person_id", referencedColumnName = "id")
    )
    private List<Person> reviewers = new ArrayList<>();

    @Column(name = "target_branch")
    private String targetBranch;

    @Column(name = "source_branch")
    private String sourceBranch;

    @Column(name = "notification")
    private boolean notification;

    @Column(name = "is_assignee")
    private boolean userAssignee;

    @Column(name = "is_reviewer")
    private boolean userReviewer;

    @ElementCollection
    @CollectionTable(name = "merge_request_label", joinColumns = @JoinColumn(name = "merge_request_id"))
    @Column(name = "label")
    private Set<String> labels = new HashSet<>();

    @Column(name = "date_last_commit")
    private LocalDateTime dateLastCommit;

}
