package org.sadtech.bot.vcs.core.domain.notify.task;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.sadtech.bot.vcs.core.domain.notify.Notify;

import java.util.Set;

@Getter
@EqualsAndHashCode(callSuper = true)
public abstract class TaskNotify extends Notify {

    protected final String authorName;
    protected final String url;
    protected final String messageTask;

    protected TaskNotify(
            Set<String> logins,
            String authorName,
            String url,
            String messageTask
    ) {
        super(logins);
        this.authorName = authorName;
        this.url = url;
        this.messageTask = messageTask;
    }

}
