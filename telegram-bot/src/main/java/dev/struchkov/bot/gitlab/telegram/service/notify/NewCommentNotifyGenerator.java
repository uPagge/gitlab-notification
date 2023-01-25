package dev.struchkov.bot.gitlab.telegram.service.notify;

import dev.struchkov.bot.gitlab.context.domain.notify.comment.NewCommentNotify;
import dev.struchkov.bot.gitlab.context.utils.Icons;
import dev.struchkov.godfather.main.domain.BoxAnswer;
import org.springframework.stereotype.Component;

import static dev.struchkov.godfather.main.domain.BoxAnswer.boxAnswer;
import static dev.struchkov.haiti.utils.Checker.checkNotNull;
import static dev.struchkov.haiti.utils.Strings.escapeMarkdown;

@Component
public class NewCommentNotifyGenerator implements NotifyBoxAnswerGenerator<NewCommentNotify> {

    @Override
    public BoxAnswer generate(NewCommentNotify notify) {
        final StringBuilder builder = new StringBuilder(Icons.COMMENT).append(" [New answer in discussion](").append(notify.getUrl()).append(")");

        if (checkNotNull(notify.getDiscussionMessage())) {
            builder.append("\n-- -- discussion first message -- --\n")
                    .append("*").append(notify.getDiscussionAuthor()).append("*: ").append(escapeMarkdown(notify.getDiscussionMessage()));
        }

        if (checkNotNull(notify.getPreviousMessage())) {
            builder.append("\n-- -- -- previous message -- -- --\n")
                    .append("*").append(notify.getPreviousAuthor()).append("*: ").append(escapeMarkdown(notify.getPreviousMessage()));
        }

        builder.append("\n-- -- -- --- new answer --- -- -- --\n")
                .append("*").append(notify.getAuthorName()).append("*: ").append(escapeMarkdown(notify.getMessage()));

        final String messageNotify = builder.toString();
        return boxAnswer(messageNotify);
    }

    @Override
    public String getNotifyType() {
        return NewCommentNotify.TYPE;
    }

}
