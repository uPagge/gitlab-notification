package org.sadtech.bot.gitlab.sdk.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class NoteJson {

    private Long id;
    private String type;
    private String body;

    @JsonProperty("created_at")
    private LocalDateTime created;

    @JsonProperty("updated_at")
    private LocalDateTime updated;

    private PersonJson author;
    private boolean system;

    @JsonProperty("noteable_id")
    private Long noteableId;

    @JsonProperty("noteable_type")
    private String noteableType;

    private Boolean resolveable;

    private Boolean resolved;

    @JsonProperty("resolved_by")
    private PersonJson resolvedBy;

    @JsonProperty("noteable_iid")
    private Long noteableIid;


}
