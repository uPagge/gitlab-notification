package com.tsc.bitbucketbot.domain.change;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.Set;

@Getter
@EqualsAndHashCode(callSuper = true)
public class UpdatePrChange extends PrChange {

    private final String author;

    @Builder
    private UpdatePrChange(
            Set<Long> telegramIds,
            String name,
            String url, String author) {
        super(ChangeType.UPDATE_PR, telegramIds, name, url);
        this.author = author;
    }

}
