package org.sadtech.bot.bitbucketbot.domain.change.task;

import lombok.Builder;
import lombok.Getter;
import org.sadtech.bot.bitbucketbot.domain.change.ChangeType;
import org.sadtech.bot.bitbucketbot.utils.Smile;

import java.text.MessageFormat;
import java.util.Set;

/**
 * // TODO: 10.09.2020 Добавить описание.
 *
 * @author upagge 10.09.2020
 */
@Getter
public class TaskNewChange extends TaskChange {

    @Builder
    protected TaskNewChange(
            Set<Long> telegramIds,
            String authorName,
            String url,
            String messageTask
    ) {
        super(ChangeType.NEW_TASK, telegramIds, authorName, url, messageTask);
    }

    @Override
    public String generateMessage() {
        return MessageFormat.format(
                "{0} *Назначена новая задача* | [ПР]({1}){2}" +
                        "{3}: {4}",
                Smile.TASK, url, Smile.HR, authorName, messageTask
        );
    }

}
