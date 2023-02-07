package dev.struchkov.bot.gitlab.telegram.service.notify;

import dev.struchkov.bot.gitlab.context.domain.notify.comment.NewCommentNotify;
import dev.struchkov.bot.gitlab.context.utils.Icons;
import dev.struchkov.godfather.main.domain.BoxAnswer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static dev.struchkov.bot.gitlab.telegram.utils.UnitName.DELETE_MESSAGE;
import static dev.struchkov.godfather.main.domain.BoxAnswer.boxAnswer;
import static dev.struchkov.godfather.main.domain.keyboard.button.SimpleButton.simpleButton;
import static dev.struchkov.godfather.telegram.domain.keyboard.InlineKeyBoard.inlineKeyBoard;
import static dev.struchkov.godfather.telegram.domain.keyboard.button.UrlButton.urlButton;
import static dev.struchkov.haiti.utils.Checker.checkNotNull;
import static dev.struchkov.haiti.utils.Strings.escapeMarkdown;

@Component
@RequiredArgsConstructor
public class NewCommentNotifyGenerator implements NotifyBoxAnswerGenerator<NewCommentNotify> {

    @Override
    public BoxAnswer generate(NewCommentNotify notify) {
        final StringBuilder builder = new StringBuilder(Icons.COMMENT).append(" *New answer in Thread*")
                .append(Icons.HR)
                .append(Icons.link(escapeMarkdown(notify.getMergeRequestName()), notify.getUrl()))
                .append("\n");

        if (checkNotNull(notify.getDiscussionMessage())) {
            builder.append("\n-- --  thread first message  -- --\n")
                    .append("*").append(notify.getDiscussionAuthor()).append("*: ").append(escapeMarkdown(notify.getDiscussionMessage()))
                    .append("\n");
        }

        if (checkNotNull(notify.getPreviousMessage())) {
            builder.append("\n-- -- -- previous message -- -- --\n")
                    .append("*").append(notify.getPreviousAuthor()).append("*: ").append(escapeMarkdown(notify.getPreviousMessage()))
                    .append("\n");
        }

        if (checkNotNull(notify.getMessage())) {
            builder.append("\n-- -- -- --- new answer --- -- -- --\n")
                    .append("*").append(notify.getAuthorName()).append("*: ").append(escapeMarkdown(notify.getMessage()))
                    .append("\n");
        }

        final String messageNotify = builder.toString();
        return boxAnswer(
                messageNotify,
                inlineKeyBoard(
                        simpleButton(Icons.VIEW, DELETE_MESSAGE),
                        urlButton(Icons.LINK, notify.getUrl())
                )
        );
    }

    @Override
    public String getNotifyType() {
        return NewCommentNotify.TYPE;
    }

}
