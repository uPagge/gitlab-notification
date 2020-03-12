package com.tsc.bitbucketbot.dto.bitbucket;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.tsc.bitbucketbot.utils.LocalDateFromEpochDeserializer;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CommentJson {

    private Long id;
    private String text;
    private UserJson author;

    @JsonDeserialize(using = LocalDateFromEpochDeserializer.class)
    private LocalDate createdDate;

    @JsonDeserialize(using = LocalDateFromEpochDeserializer.class)
    private LocalDate updatedDate;

}
