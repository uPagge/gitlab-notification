package dev.struchkov.bot.gitlab.context.domain.notify.task;

import dev.struchkov.bot.gitlab.context.service.AppSettingService;
import dev.struchkov.bot.gitlab.context.utils.Smile;
import lombok.Builder;

import static dev.struchkov.haiti.utils.Strings.escapeMarkdown;

/**
 * @author upagge 10.09.2020
 */
public class TaskCloseNotify extends TaskNotify {

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
    public String generateMessage(AppSettingService settingService) {
        return settingService.getMessage(
                "notify.task.close",
                Smile.TASK.getValue(), url, Smile.HR.getValue(), authorName, escapeMarkdown(messageTask), personTasks, personResolvedTasks
        );
    }

}
