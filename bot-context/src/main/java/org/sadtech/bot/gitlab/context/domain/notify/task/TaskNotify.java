package org.sadtech.bot.gitlab.context.domain.notify.task;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.sadtech.bot.gitlab.context.domain.EntityType;
import org.sadtech.bot.gitlab.context.domain.notify.Notify;

import java.util.Set;

@Getter
@EqualsAndHashCode(callSuper = true)
public abstract class TaskNotify extends Notify {

    protected final String authorName;
    protected final String url;
    protected final String messageTask;

    protected TaskNotify(
            Set<String> recipients,
            String authorName,
            String url,
            String messageTask
    ) {
        super(EntityType.PERSON, recipients);
        this.authorName = authorName;
        this.url = url;
        this.messageTask = messageTask;
    }

}
