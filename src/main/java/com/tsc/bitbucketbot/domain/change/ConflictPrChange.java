package com.tsc.bitbucketbot.domain.change;

import lombok.Builder;
import lombok.Getter;

import java.util.Set;

@Getter
public class ConflictPrChange extends PrChange {

    @Builder
    private ConflictPrChange(
            Set<Long> telegramIds,
            String name,
            String url
    ) {
        super(ChangeType.CONFLICT_PR, telegramIds, name, url);
    }

}
