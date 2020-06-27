package org.sadtech.bot.bitbucketbot.domain.change.pullrequest;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.sadtech.bot.bitbucketbot.domain.change.Change;
import org.sadtech.bot.bitbucketbot.domain.change.ChangeType;

import java.util.Set;

@Getter
@EqualsAndHashCode(callSuper = true)
public abstract class PrChange extends Change {

    private final String title;
    private final String url;

    protected PrChange(ChangeType type, Set<Long> telegramIds, String title, String url) {
        super(type, telegramIds);
        this.title = title;
        this.url = url;
    }

}
