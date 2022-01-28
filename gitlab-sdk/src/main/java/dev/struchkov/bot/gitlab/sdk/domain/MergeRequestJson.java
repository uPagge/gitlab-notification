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
 * @author upagge [30.01.2020]
 */
@Data
public class MergeRequestJson {

    private Long id;

    @JsonProperty("iid")
    private Long twoId;

    @JsonProperty("project_id")
    private Long projectId;
    private String title;
    private String description;
    private MergeRequestStateJson state;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonProperty("created_at")
    private LocalDateTime createdDate;

    @JsonProperty("updated_at")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime updatedDate;

    private PersonJson author;
    private PersonJson assignee;

    @JsonProperty("web_url")
    private String webUrl;

    @JsonProperty("has_conflicts")
    private boolean conflicts;

    @JsonProperty("target_branch")
    private String targetBranch;

    @JsonProperty("source_branch")
    private String sourceBranch;

    private Set<String> labels;

}
