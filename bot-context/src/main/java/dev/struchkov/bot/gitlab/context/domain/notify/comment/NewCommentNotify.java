package dev.struchkov.bot.gitlab.context.domain.notify.comment;

import dev.struchkov.bot.gitlab.context.domain.notify.Notify;
import dev.struchkov.bot.gitlab.context.utils.Smile;
import lombok.Builder;

import static dev.struchkov.haiti.utils.Checker.checkNotNull;
import static dev.struchkov.haiti.utils.Strings.escapeMarkdown;

@Builder
public record NewCommentNotify(
        String url,
        String discussionMessage,
        String discussionAuthor,
        String previousMessage,
        String previousAuthor,
        String authorName,
        String message
) implements Notify {

    @Override
    public String generateMessage() {

        final StringBuilder builder = new StringBuilder(Smile.COMMENT.getValue()).append(" [New answer in discussion](").append(url).append(")\n---  ---  ---  ---");

        if (checkNotNull(discussionMessage)) {
            builder.append("\n-- -- discussion first message -- --\n")
                    .append("*").append(discussionAuthor).append("*: ").append(escapeMarkdown(discussionMessage));
        }

        if (checkNotNull(previousMessage)) {
            builder.append("\n-- -- -- previous message -- -- --\n")
                    .append("*").append(previousAuthor).append("*: ").append(escapeMarkdown(previousMessage));
        }

        builder.append("\n-- -- -- --- new answer --- -- -- --\n")
                .append("*").append(authorName).append("*: ").append(escapeMarkdown(message));
        return builder.toString();
    }

}


