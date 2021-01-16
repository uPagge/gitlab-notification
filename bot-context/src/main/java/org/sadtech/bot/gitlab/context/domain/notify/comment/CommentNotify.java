package org.sadtech.bot.gitlab.context.domain.notify.comment;

import lombok.Builder;
import lombok.Getter;
import org.sadtech.bot.gitlab.context.domain.notify.Notify;
import org.sadtech.bot.gitlab.context.service.AppSettingService;
import org.sadtech.bot.gitlab.context.utils.Smile;

@Getter
public class CommentNotify extends Notify {

    private final String authorName;
    private final String message;
    private final String url;

    @Builder
    private CommentNotify(
            String url,
            String authorName,
            String message
    ) {
        this.authorName = authorName;
        this.message = message;
        this.url = url;
    }

    @Override
    public String generateMessage(AppSettingService settingService) {
        return settingService.getMessage(
                "notify.comment.bell",
                Smile.COMMENT.getValue(), url, Smile.HR.getValue(), authorName, escapeMarkdown(message)
        );
    }

}


