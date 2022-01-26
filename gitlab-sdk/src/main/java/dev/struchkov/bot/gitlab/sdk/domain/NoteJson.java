package dev.struchkov.bot.gitlab.sdk.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class NoteJson {

    private Long id;
    private String type;
    private String body;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonProperty("created_at")
    private LocalDateTime created;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonProperty("updated_at")
    private LocalDateTime updated;

    private PersonJson author;
    private boolean system;

    @JsonProperty("noteable_id")
    private Long noteableId;

    @JsonProperty("noteable_type")
    private String noteableType;

    private boolean resolvable;

    private Boolean resolved;

    @JsonProperty("resolved_by")
    private PersonJson resolvedBy;

    @JsonProperty("noteable_iid")
    private Long noteableIid;


}
