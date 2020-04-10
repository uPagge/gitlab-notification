package com.tsc.bitbucketbot.domain.change;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.Set;

@Getter
@EqualsAndHashCode(callSuper = true)
public class CommentChange extends Change {

    private final String authorName;
    private final String message;
    private final String url;

    @Builder
    private CommentChange(
            Set<Long> telegramIds,
            String url,
            String authorName,
            String message
    ) {
        super(ChangeType.NEW_COMMENT, telegramIds);
        this.authorName = authorName;
        this.message = message;
        this.url = url;
    }

}


