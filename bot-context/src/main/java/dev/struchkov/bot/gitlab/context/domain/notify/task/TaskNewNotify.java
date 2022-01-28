package dev.struchkov.bot.gitlab.context.domain.notify.task;

import dev.struchkov.bot.gitlab.context.service.AppSettingService;
import dev.struchkov.bot.gitlab.context.utils.Smile;
import lombok.Builder;
import lombok.Getter;

import static dev.struchkov.haiti.utils.Strings.escapeMarkdown;

/**
 * @author upagge 10.09.2020
 */
@Getter
public class TaskNewNotify extends TaskNotify {

    @Builder
    protected TaskNewNotify(
            String authorName,
            String url,
            String messageTask
    ) {
        super(authorName, url, messageTask);
    }

    @Override
    public String generateMessage(AppSettingService settingService) {
        return settingService.getMessage(
                "notify.task.new",
                Smile.TASK.getValue(), url, Smile.HR.getValue(), authorName, escapeMarkdown(messageTask)
        );
    }

}
