package dev.struchkov.bot.gitlab.context.domain.notify.task;

import dev.struchkov.haiti.utils.fieldconstants.annotation.FieldNames;
import lombok.Builder;
import lombok.Getter;

import static dev.struchkov.bot.gitlab.context.domain.notify.task.TaskCloseNotifyFields.CLASS_NAME;

/**
 * @author upagge 10.09.2020
 */
@Getter
@FieldNames
public class TaskCloseNotify extends TaskNotify {

    public static final String TYPE = CLASS_NAME;

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
