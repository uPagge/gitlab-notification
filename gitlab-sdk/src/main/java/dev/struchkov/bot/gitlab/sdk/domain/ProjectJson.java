package dev.struchkov.bot.gitlab.sdk.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author upagge 14.01.2021
 */
@Data
public class ProjectJson {

    private Long id;
    private String name;
    private String description;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonProperty("created_at")
    private LocalDateTime createdDate;

    @JsonProperty("web_url")
    private String webUrl;

    @JsonProperty("ssh_url_to_repo")
    private String sshUrlToRepo;

    @JsonProperty("http_url_to_repo")
    private String httpUrlToRepo;

    @JsonProperty("creator_id")
    private Long creatorId;

}
