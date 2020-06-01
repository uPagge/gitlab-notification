package org.sadtech.bot.bitbucketbot.dto.bitbucket;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;
import org.sadtech.bot.bitbucketbot.utils.LocalDateTimeFromEpochDeserializer;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class CommentJson {

    private Long id;
    private String text;
    private UserJson author;
    private List<CommentJson> comments;

    @JsonDeserialize(using = LocalDateTimeFromEpochDeserializer.class)
    private LocalDateTime createdDate;

    @JsonDeserialize(using = LocalDateTimeFromEpochDeserializer.class)
    private LocalDateTime updatedDate;

}
