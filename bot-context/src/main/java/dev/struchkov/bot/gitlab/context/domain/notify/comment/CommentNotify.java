package dev.struchkov.bot.gitlab.context.domain.notify.comment;

import dev.struchkov.bot.gitlab.context.domain.notify.Notify;
import dev.struchkov.bot.gitlab.context.utils.Smile;
import lombok.Builder;

import java.text.MessageFormat;

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
    public String generateMessage() {
        return MessageFormat.format(
                "{0} *New mention* | [MR]({1}){2}*{3}*: {4}",
                Smile.COMMENT.getValue(), url, Smile.HR.getValue(), authorName, escapeMarkdown(message)
        );
    }

}


