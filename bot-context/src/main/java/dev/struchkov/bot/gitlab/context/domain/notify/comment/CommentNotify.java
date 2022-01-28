package dev.struchkov.bot.gitlab.context.domain.notify.comment;

import dev.struchkov.bot.gitlab.context.domain.notify.Notify;
import dev.struchkov.bot.gitlab.context.service.AppSettingService;
import dev.struchkov.bot.gitlab.context.utils.Smile;
import lombok.Builder;

import static dev.struchkov.haiti.utils.Strings.escapeMarkdown;

public record CommentNotify(
        String url,
        String authorName,
        String message
) implements Notify {

    @Builder
    public CommentNotify {
    }

    @Override
    public String generateMessage(AppSettingService settingService) {
        return settingService.getMessage(
                "notify.comment.bell",
                Smile.COMMENT.getValue(), url, Smile.HR.getValue(), authorName, escapeMarkdown(message)
        );
    }

}


