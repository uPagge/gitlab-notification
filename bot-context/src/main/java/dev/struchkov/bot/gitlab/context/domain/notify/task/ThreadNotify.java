package dev.struchkov.bot.gitlab.context.domain.notify.task;

import dev.struchkov.bot.gitlab.context.domain.notify.Notify;
import lombok.Getter;

@Getter
public abstract class ThreadNotify implements Notify {

    protected final String mergeRequestName;
    protected final String authorName;
    protected final String url;
    protected final String messageTask;

    protected ThreadNotify(
            String mergeRequestName,
            String authorName,
            String url,
            String messageTask
    ) {
        this.mergeRequestName = mergeRequestName;
        this.authorName = authorName;
        this.url = url;
        this.messageTask = messageTask;
    }

}
