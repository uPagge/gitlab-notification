package com.tsc.bitbucketbot.domain.change;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.Set;

@Getter
@EqualsAndHashCode(callSuper = true)
public class NewPrChange extends PrChange {

    private final String description;
    private final String author;

    @Builder
    private NewPrChange(
            Set<Long> telegramIds,
            String name,
            String url,
            String description,
            String author) {
        super(ChangeType.NEW_PR, telegramIds, name, url);
        this.description = description;
        this.author = author;
    }

}
