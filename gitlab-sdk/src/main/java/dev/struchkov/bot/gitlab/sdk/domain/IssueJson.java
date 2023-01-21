package dev.struchkov.bot.gitlab.sdk.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * @author Dmitry Sheyko [17.01.2023]
 */
@Data
public class IssueJson {

    private Long id;

    @JsonProperty("iid")
    private Long twoId;

    @JsonProperty("project_id")
    private Long projectId;
    private String title;
    private String description;
    private IssueStateJson state;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonProperty("created_at")
    private LocalDateTime createdDate;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonProperty("updated_at")
    private LocalDateTime updatedDate;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonProperty("closed_at")
    private LocalDateTime closedDate;

    @JsonProperty("closed_by")
    private PersonJson closedBy;

    private Set<String> labels;
    private MilestoneJson milestone;
    private Set<PersonJson> assignees;
    private PersonJson author;
    private IssueTypeJson type;
    private PersonJson assignee;

    @JsonProperty("user_notes_count")
    private Integer userNotesCount;

    @JsonProperty("merge_requests_count")
    private Integer mergeRequestsCount;

    @JsonProperty("upvotes")
    private Integer upVotes;

    @JsonProperty("downvotes")
    private Integer downVotes;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonProperty("due_date")
    private LocalDateTime dueDate;
    private Boolean confidential;

    @JsonProperty("discussion_locked")
    private Integer discussionLocked;

    @JsonProperty("issue_type")
    private String issueType;

    @JsonProperty("web_url")
    private String webUrl;

    @JsonProperty("time_stats")
    private TimeStatsJson timeStats;

    @JsonProperty("task_completion_status")
    private TaskCompletionStatusJson taskCompletionStatus;

    @JsonProperty("blocking_issues_count")
    private Integer blockingIssuesCount;

    @JsonProperty("has_tasks")
    private Boolean hasTasks;

    @JsonProperty("_links")
    private LinksJson links;

    private ReferencesJson references;
    private String severity;

    @JsonProperty("moved_to_id")
    private Long movedToId;

    @JsonProperty("service_desk_reply_to")
    private Long serviceDescReplyTo;

    @JsonProperty("epic_issue_id")
    private Long epicId;

}