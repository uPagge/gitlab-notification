package com.tsc.bitbucketbot.dto.bitbucket;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.tsc.bitbucketbot.utils.LocalDateTimeFromEpochDeserializer;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * TODO: Добавить описание класса.
 *
 * @author upagge [30.01.2020]
 */
@Data
public class PullRequestJson {

    private Long id;
    private Integer version;
    private PullRequestState state;

    @JsonDeserialize(using = LocalDateTimeFromEpochDeserializer.class)
    private LocalDateTime createdDate;

    @JsonDeserialize(using = LocalDateTimeFromEpochDeserializer.class)
    private LocalDateTime updatedDate;

    private String title;
    private String description;
    private LinkJson links;
    private UserDecisionJson author;
    private List<UserDecisionJson> reviewers;
    private FromRefJson fromRef;
    private Properties properties;

}
