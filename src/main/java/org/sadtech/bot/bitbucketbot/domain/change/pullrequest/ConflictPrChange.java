package org.sadtech.bot.bitbucketbot.domain.change.pullrequest;

import lombok.Builder;
import lombok.Getter;
import org.sadtech.bot.bitbucketbot.domain.change.ChangeType;

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
