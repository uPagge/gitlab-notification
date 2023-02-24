package dev.struchkov.bot.gitlab.telegram.service.notify;

import dev.struchkov.bot.gitlab.context.domain.notify.task.TaskCloseNotify;
import dev.struchkov.bot.gitlab.context.utils.Icons;
import dev.struchkov.godfather.simple.domain.BoxAnswer;
import org.springframework.stereotype.Component;

import static dev.struchkov.bot.gitlab.context.utils.Icons.link;
import static dev.struchkov.godfather.simple.domain.BoxAnswer.boxAnswer;
import static dev.struchkov.haiti.utils.Strings.escapeMarkdown;

@Component
public class ThreadCloseNotifyGenerate implements NotifyBoxAnswerGenerator<TaskCloseNotify> {

    @Override
    public BoxAnswer generate(TaskCloseNotify notify) {

        final StringBuilder builder = new StringBuilder(Icons.THREAD).append(" *Closed ").append(link("task", notify.getUrl()))
                .append(Icons.HR)
                .append("*").append(notify.getAuthorName()).append("*: ").append(escapeMarkdown(notify.getMessageTask()));

        final String notifyMessage = builder.toString();
        return boxAnswer(notifyMessage);
    }

    @Override
    public String getNotifyType() {
        return TaskCloseNotify.TYPE;
    }

}
