package dev.struchkov.bot.gitlab.context.domain.notify.task;

import lombok.Builder;
import lombok.Getter;

/**
 * @author upagge 10.09.2020
 */
@Getter
public class TaskCloseNotify extends TaskNotify {

    public static final String TYPE = "TaskCloseNotify";

    private final Long personTasks;
    private final Long personResolvedTasks;

    @Builder
    protected TaskCloseNotify(
            String authorName,
            String url,
            String messageTask,
            Long personTasks,
            Long personResolvedTasks
    ) {
        super(authorName, url, messageTask);
        this.personTasks = personTasks;
        this.personResolvedTasks = personResolvedTasks;
    }

    @Override
    public String getType() {
        return TYPE;
    }

}
