package dev.struchkov.bot.gitlab.context.domain.notify.task;

import dev.struchkov.bot.gitlab.context.domain.notify.Notify;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode(callSuper = true)
public abstract class TaskNotify extends Notify {

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
