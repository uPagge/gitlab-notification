package org.sadtech.bot.bitbucketbot.domain.change.pullrequest;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.sadtech.bot.bitbucketbot.domain.change.ChangeType;

import java.util.Set;

@Getter
@EqualsAndHashCode(callSuper = true)
public class NewPrChange extends PrChange {

    private final String description;
    private final String author;

    @Builder
    private NewPrChange(
            Set<Long> telegramIds,
            String title,
            String url,
            String description,
            String author) {
        super(ChangeType.NEW_PR, telegramIds, title, url);
        this.description = description;
        this.author = author;
    }

}
