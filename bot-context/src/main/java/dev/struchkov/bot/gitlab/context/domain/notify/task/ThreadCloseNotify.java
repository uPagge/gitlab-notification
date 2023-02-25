package dev.struchkov.bot.gitlab.context.domain.notify.task;

import dev.struchkov.haiti.utils.fieldconstants.annotation.FieldNames;
import lombok.Builder;
import lombok.Getter;

import static dev.struchkov.bot.gitlab.context.domain.notify.task.ThreadCloseNotifyFields.CLASS_NAME;

/**
 * @author upagge 10.09.2020
 */
@Getter
@FieldNames
public class ThreadCloseNotify extends ThreadNotify {

    public static final String TYPE = CLASS_NAME;

    private final Long personTasks;
    private final Long personResolvedTasks;
    private final String authorLastNote;
    private final String messageLastNote;

    @Builder
    protected ThreadCloseNotify(
            String mergeRequestName,
            String authorName,
            String url,
            String messageTask,
            Long personTasks,
            Long personResolvedTasks,
            String authorLastNote,
            String messageLastNote
    ) {
        super(mergeRequestName, authorName, url, messageTask);
        this.personTasks = personTasks;
        this.personResolvedTasks = personResolvedTasks;
        this.authorLastNote = authorLastNote;
        this.messageLastNote = messageLastNote;
    }

    @Override
    public String getType() {
        return TYPE;
    }

}
