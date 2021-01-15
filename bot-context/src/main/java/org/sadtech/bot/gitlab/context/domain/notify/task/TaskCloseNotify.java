package org.sadtech.bot.gitlab.context.domain.notify.task;

import lombok.Builder;
import org.sadtech.bot.gitlab.context.utils.Smile;

import java.text.MessageFormat;

/**
 * // TODO: 10.09.2020 Добавить описание.
 *
 * @author upagge 10.09.2020
 */
public class TaskCloseNotify extends TaskNotify {

    @Builder
    protected TaskCloseNotify(
            String authorName,
            String url,
            String messageTask
    ) {
        super(authorName, url, messageTask);
    }

    @Override
    public String generateMessage() {
        return MessageFormat.format(
                "{0} *Закрыта* [задача]({1}){2}" +
                        "*{3}*: {4}",
                Smile.TASK, url, Smile.HR, authorName, escapeMarkdown(messageTask)
        );
    }

}
