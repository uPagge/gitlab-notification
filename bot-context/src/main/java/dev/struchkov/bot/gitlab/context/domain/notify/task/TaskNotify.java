package dev.struchkov.bot.gitlab.context.domain.notify.task;

import dev.struchkov.bot.gitlab.context.domain.notify.Notify;
import lombok.Getter;

@Getter
public abstract class TaskNotify implements Notify {

    protected final String authorName;
    protected final String url;
    protected final String messageTask;

    protected TaskNotify(
            String authorName,
            String url,
            String messageTask
    ) {
        this.authorName = authorName;
        this.url = url;
        this.messageTask = messageTask;
    }

}
