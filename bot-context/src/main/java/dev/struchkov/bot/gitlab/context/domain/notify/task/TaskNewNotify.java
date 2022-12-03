package dev.struchkov.bot.gitlab.context.domain.notify.task;

import dev.struchkov.bot.gitlab.context.utils.Smile;
import lombok.Builder;
import lombok.Getter;

import java.text.MessageFormat;

import static dev.struchkov.haiti.utils.Strings.escapeMarkdown;

/**
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
                "{0} *New [task]({1}) assigned{2}*{3}*: {4}",
                Smile.TASK.getValue(), url, Smile.HR.getValue(), authorName, escapeMarkdown(messageTask)
        );
    }

}
