package com.tsc.bitbucketbot.domain.change;

import lombok.Builder;
import lombok.Getter;
import lombok.Singular;

import java.util.Set;

@Getter
public class ConflictPrChange extends PrChange {

    @Builder
    private ConflictPrChange(
            @Singular("telegramId") Set<Long> telegramId,
            String name,
            String url
    ) {
        super(ChangeType.CONFLICT_PR, telegramId, name, url);
    }

}
