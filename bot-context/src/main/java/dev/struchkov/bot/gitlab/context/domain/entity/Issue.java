package dev.struchkov.bot.gitlab.context.domain.entity;

import dev.struchkov.bot.gitlab.context.domain.IssueState;
import dev.struchkov.bot.gitlab.context.domain.IssueType;
import dev.struchkov.haiti.utils.fieldconstants.annotation.FieldNames;
import dev.struchkov.haiti.utils.fieldconstants.domain.Mode;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;


/**
 * Сущность Issue.
 *
 * @author Dmitry Sheyko [17.01.2023]
 */

// При запросе issue учесть что пагинация по умолчанию - 20 объектов
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
    @CollectionTable(name = "issue_labels", joinColumns = @JoinColumn(name = "label_id"))
    @Column(name = "labels")
    private Set<String> labels = new HashSet<>();

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "milestone_id")
    private Milestone milestone;

    @Column(name = "assignees")
    @OneToMany(
            fetch = FetchType.LAZY,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE}
    )
    @JoinTable(
            name = "issue_assignees",
            joinColumns = @JoinColumn(name = "issue_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "person_id", referencedColumnName = "id")
    )
    private Set<Person> assignees = new HashSet<>();

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "author_id")
    private Person author;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "type")
    private IssueType type;   // ОБразец приходящего значения "INCIDENT"

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "assignee_id")
    private Person assignee;

    @Column(name = "user_notes_count")
    private Integer userNotesCount;

    @Column(name = "merge_requests_count")
    private Integer mergeRequestsCount;

    @Column(name = "up_votes")
    private Integer upVotes;

    @Column(name = "down_votes")
    private Integer downVotes;

    @Column(name = "due_date")
    private LocalDateTime dueDate;

    @Column(name = "confidential")
    private Boolean confidential;

    @Column(name = "discussion_locked")
    private Integer discussionLocked;

    @Column(name = "issue_type")
    private String issueType; //TODO выяснить зачем дублирует поле type Образец приходящего значения "incident"

    @Column(name = "web_url")
    private String webUrl;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "timeEstimate", column = @Column(name = "time_estimate")),
            @AttributeOverride(name = "totalTimeSpent", column = @Column(name = "total_time_spent")),
            @AttributeOverride(name = "humanTimeEstimate", column = @Column(name = "human_time_estimate")),
            @AttributeOverride(name = "humanTotalTimeSpent", column = @Column(name = "human_total_time_spent"))
    })
    private TimeStats timeStats;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "count", column = @Column(name = "task_count")),
            @AttributeOverride(name = "completedCount", column = @Column(name = "task_completed_count"))
    })
    private TaskCompletionStatus taskCompletionStatus;

    @Column(name = "blocking_issues_count")
    private Integer blockingIssuesCount;

    @Column(name = "has_tasks")
    private Boolean hasTasks;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "self", column = @Column(name = "link_to_self")),
            @AttributeOverride(name = "notes", column = @Column(name = "link_to_notes")),
            @AttributeOverride(name = "awardEmoji", column = @Column(name = "link_to_award_emoji")),
            @AttributeOverride(name = "project", column = @Column(name = "link_to_project")),
            @AttributeOverride(name = "closedAsDuplicateOf", column = @Column(name = "link_to_closed_as_duplicate_of"))
    })
    private Links links;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "shortReference", column = @Column(name = "short_reference")),
            @AttributeOverride(name = "relativeReference", column = @Column(name = "relative_reference")),
            @AttributeOverride(name = "fullReference", column = @Column(name = "full_reference"))
    })
    private References references;

    /**
    Возможно надо заменить на енум: "UNKNOWN",  "Critical - S1", "High - S2", "Medium - S3", "Low - S4".
    Но выбор любых значений кроме "UNKNOWN" доступен только для премиум акаунтов и я не могу получить точные значения котоые оно принимает.
    */
    @Column(name = "severity")
    private String severity;

    @Column(name = "moved_to_id")
    private Long movedToId;

    @Column(name = "service_desk_reply_to")
    private String serviceDescReplyTo;

    @Column(name = "epic_issue_id")
    private Long epicId; // "epic_issue_id" Поле доснтупное только для премиум акаунтов
}