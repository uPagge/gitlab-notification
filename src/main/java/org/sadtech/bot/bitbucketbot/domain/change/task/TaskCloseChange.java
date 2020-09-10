package org.sadtech.bot.bitbucketbot.domain.change.task;

import lombok.Builder;
import org.sadtech.bot.bitbucketbot.domain.change.ChangeType;
import org.sadtech.bot.bitbucketbot.utils.Smile;

import java.text.MessageFormat;
import java.util.Set;

/**
 * // TODO: 10.09.2020 Добавить описание.
 *
 * @author upagge 10.09.2020
 */
public class TaskCloseChange extends TaskChange {

    @Builder
    protected TaskCloseChange(
            Set<Long> telegramIds,
            String authorName,
            String url,
            String messageTask
    ) {
        super(ChangeType.RESOLVED_TASK, telegramIds, authorName, url, messageTask);
    }

    @Override
    public String generateMessage() {
        return MessageFormat.format(
                "{0} Задача выполнена* | [ПР]({1}){2}" +
                        "{3}: {4}",
                Smile.TASK, url, Smile.HR, authorName, messageTask
        );
    }

}
