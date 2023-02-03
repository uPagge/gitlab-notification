package dev.struchkov.bot.gitlab.context.domain.entity;

import dev.struchkov.bot.gitlab.context.domain.IssueState;
import dev.struchkov.bot.gitlab.context.domain.IssueType;
import dev.struchkov.haiti.utils.fieldconstants.annotation.FieldNames;
import dev.struchkov.haiti.utils.fieldconstants.domain.Mode;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Сущность Issue.
 *
 * @author Dmitry Sheyko [17.01.2023]
 */
@Getter
@Setter
@Entity
@FieldNames(mode = {Mode.TABLE, Mode.SIMPLE})
@Table(name = "issue")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Issue {

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

    @Enumerated(value = EnumType.STRING)
    @Column(name = "state")
    private IssueState state;

    @Column(name = "created_date")
    private LocalDateTime createdDate;

    @Column(name = "updated_date")
    private LocalDateTime updatedDate;

    @Column(name = "closed_at")
    private LocalDateTime closeDate;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "closed_by_id")
    private Person closedBy;

    @ElementCollection
    @CollectionTable(name = "issue_label", joinColumns = @JoinColumn(name = "issue_id"))
    @Column(name = "label")
    private Set<String> labels = new HashSet<>();

    @OneToMany(
            fetch = FetchType.LAZY,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE}
    )
    @JoinTable(
            name = "issue_assignees",
            joinColumns = @JoinColumn(name = "issue_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "person_id", referencedColumnName = "id")
    )
    private List<Person> assignees = new ArrayList<>();

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "author_id")
    private Person author;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "type")
    private IssueType type;

    @Column(name = "user_notes_count")
    private Integer userNotesCount;

    @Column(name = "merge_requests_count")
    private Integer mergeRequestsCount;

    @Column(name = "up_votes")
    private Integer upVotes;

    @Column(name = "down_votes")
    private Integer downVotes;

    @Column(name = "due_date")
    private LocalDate dueDate;

    @Column(name = "confidential")
    private Boolean confidential;

    @Column(name = "discussion_locked")
    private Integer discussionLocked;

    @Column(name = "task_count")
    private Integer taskCount;

    @Column(name = "task_completed_count")
    private Integer taskCompletedCount;

    @Column(name = "web_url")
    private String webUrl;

    @Column(name = "blocking_issues_count")
    private Integer blockingIssuesCount;

    @Column(name = "has_tasks")
    private Boolean hasTasks;

    @Column(name = "notification")
    private boolean notification;

    @Column(name = "is_assignee")
    private boolean userAssignee;

}