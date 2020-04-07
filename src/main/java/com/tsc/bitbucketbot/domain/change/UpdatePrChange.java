package com.tsc.bitbucketbot.domain.change;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Singular;

import java.util.Set;

@Getter
@EqualsAndHashCode(callSuper = true)
public class UpdatePrChange extends PrChange {

    private final String author;

    @Builder
    private UpdatePrChange(
            @Singular("telegramId") Set<Long> telegramId,
            String name,
            String url, String author) {
        super(ChangeType.UPDATE_PR, telegramId, name, url);
        this.author = author;
    }

}
