package com.tsc.bitbucketbot.dto.bitbucket;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.tsc.bitbucketbot.utils.LocalDateFromEpochDeserializer;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class CommentJson {

    private Long id;
    private String text;
    private UserJson author;
    private List<CommentJson> comments;

    @JsonDeserialize(using = LocalDateFromEpochDeserializer.class)
    private LocalDate createdDate;

    @JsonDeserialize(using = LocalDateFromEpochDeserializer.class)
    private LocalDate updatedDate;

}
