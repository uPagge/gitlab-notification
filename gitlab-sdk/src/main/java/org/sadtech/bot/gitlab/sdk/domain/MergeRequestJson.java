package org.sadtech.bot.gitlab.sdk.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;
import org.sadtech.bot.gitlab.sdk.utils.LocalDateTimeFromEpochDeserializer;

import java.time.LocalDateTime;

/**
 * TODO: Добавить описание класса.
 *
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
    private PullRequestState state;

    @JsonProperty("created_at")
    @JsonDeserialize(using = LocalDateTimeFromEpochDeserializer.class)
    private LocalDateTime createdDate;

    @JsonProperty("updated_at")
    @JsonDeserialize(using = LocalDateTimeFromEpochDeserializer.class)
    private LocalDateTime updatedDate;

    private AuthorJson author;

    @JsonProperty("web_url")
    private String webUrl;

    @JsonProperty("has_conflicts")
    private String conflicts;

}
