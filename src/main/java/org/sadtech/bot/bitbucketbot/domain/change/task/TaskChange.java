package org.sadtech.bot.bitbucketbot.domain.change.task;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.sadtech.bot.bitbucketbot.domain.change.Change;
import org.sadtech.bot.bitbucketbot.domain.change.ChangeType;

import java.util.Set;

@Getter
@EqualsAndHashCode(callSuper = true)
public class TaskChange extends Change {

    protected final String authorName;
    protected final String url;
    protected final String messageTask;

    @Builder
    protected TaskChange(
            ChangeType type,
            Set<Long> telegramIds,
            String authorName,
            String url,
            String messageTask
    ) {
        super(type, telegramIds);
        this.authorName = authorName;
        this.url = url;
        this.messageTask = messageTask;
    }
}
