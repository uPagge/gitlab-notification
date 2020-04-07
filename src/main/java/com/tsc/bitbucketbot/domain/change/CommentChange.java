package com.tsc.bitbucketbot.domain.change;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Singular;

import java.util.Set;

@Getter
@EqualsAndHashCode(callSuper = true)
public class CommentChange extends PrChange {

    private final String authorName;
    private final String message;

    @Builder
    private CommentChange(
            @Singular("telegramId") Set<Long> telegramId,
            String name,
            String url,
            String authorName,
            String message
    ) {
        super(ChangeType.NEW_COMMENT, telegramId, name, url);
        this.authorName = authorName;
        this.message = message;
    }

}
