package org.sadtech.bot.gitlab.context.domain.notify.task;

import lombok.Builder;
import lombok.Getter;
import org.sadtech.bot.gitlab.context.utils.Smile;

import java.text.MessageFormat;

/**
 * // TODO: 10.09.2020 Добавить описание.
 *
 * @author upagge 10.09.2020
 */
@Getter
public class TaskNewNotify extends TaskNotify {

    @Builder
    protected TaskNewNotify(
            String authorName,
            String url,
            String messageTask
    ) {
        super(authorName, url, messageTask);
    }

    @Override
    public String generateMessage() {
        return MessageFormat.format(
                "{0} *Назначена новая* [задача]({1}){2}" +
                        "*{3}*: {4}",
                Smile.TASK, url, Smile.HR, authorName, escapeMarkdown(messageTask)
        );
    }

}
