package dev.struchkov.bot.gitlab.telegram.service.notify;

import dev.struchkov.bot.gitlab.context.domain.notify.task.ThreadCloseNotify;
import dev.struchkov.bot.gitlab.context.utils.Icons;
import dev.struchkov.godfather.simple.domain.BoxAnswer;
import org.springframework.stereotype.Component;

import static dev.struchkov.bot.gitlab.telegram.utils.UnitName.DELETE_MESSAGE;
import static dev.struchkov.godfather.main.domain.keyboard.button.SimpleButton.simpleButton;
import static dev.struchkov.godfather.simple.domain.BoxAnswer.boxAnswer;
import static dev.struchkov.godfather.telegram.domain.keyboard.InlineKeyBoard.inlineKeyBoard;
import static dev.struchkov.godfather.telegram.domain.keyboard.button.UrlButton.urlButton;
import static dev.struchkov.haiti.utils.Checker.checkNotBlank;
import static dev.struchkov.haiti.utils.Strings.escapeMarkdown;

@Component
public class ThreadCloseNotifyGenerate implements NotifyBoxAnswerGenerator<ThreadCloseNotify> {

    @Override
    public BoxAnswer generate(ThreadCloseNotify notify) {

        final StringBuilder builder = new StringBuilder(Icons.THREAD).append(" *Closed thread*")
                .append("\n-- -- -- merge request -- -- --\n")
                .append(escapeMarkdown(notify.getMergeRequestName()))
                .append("\n");

        if (checkNotBlank(notify.getAuthorName())) {
            builder
                    .append("\n-- -- -- thread message -- -- --\n")
                    .append("*").append(escapeMarkdown(notify.getAuthorName())).append("*: ").append(escapeMarkdown(notify.getMessageTask()))
                    .append("\n");
        }

        if (checkNotBlank(notify.getAuthorLastNote())) {
            builder
                    .append("\n-- -- -- last message -- -- -- --\n")
                    .append("*").append(escapeMarkdown(notify.getAuthorLastNote())).append("*: ").append(escapeMarkdown(notify.getMessageLastNote()));
        }

        final String notifyMessage = builder.toString();
        return boxAnswer(
                notifyMessage,
                inlineKeyBoard(
                        simpleButton(Icons.VIEW, DELETE_MESSAGE),
                        urlButton(Icons.LINK, notify.getUrl())
                )
        );
    }

    @Override
    public String getNotifyType() {
        return ThreadCloseNotify.TYPE;
    }

}
