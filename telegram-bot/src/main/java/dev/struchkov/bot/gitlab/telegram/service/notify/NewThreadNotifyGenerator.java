package dev.struchkov.bot.gitlab.telegram.service.notify;

import dev.struchkov.bot.gitlab.context.domain.notify.task.DiscussionNewNotify;
import dev.struchkov.bot.gitlab.context.utils.Icons;
import dev.struchkov.godfather.simple.domain.BoxAnswer;
import dev.struchkov.haiti.utils.container.Pair;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

import static dev.struchkov.bot.gitlab.telegram.utils.Const.BUTTON_ARG_CONFIRMATION;
import static dev.struchkov.bot.gitlab.telegram.utils.Const.BUTTON_ARG_DISABLE_NOTIFY_THREAD_ID;
import static dev.struchkov.bot.gitlab.telegram.utils.Const.BUTTON_VALUE_FALSE;
import static dev.struchkov.bot.gitlab.telegram.utils.UnitName.DELETE_MESSAGE;
import static dev.struchkov.godfather.main.domain.keyboard.button.SimpleButton.simpleButton;
import static dev.struchkov.godfather.simple.domain.BoxAnswer.boxAnswer;
import static dev.struchkov.godfather.telegram.domain.keyboard.InlineKeyBoard.inlineKeyBoard;
import static dev.struchkov.godfather.telegram.domain.keyboard.button.UrlButton.urlButton;
import static dev.struchkov.haiti.utils.Checker.checkNotBlank;
import static dev.struchkov.haiti.utils.Checker.checkNotEmpty;
import static dev.struchkov.haiti.utils.Strings.escapeMarkdown;

@Component
public class NewThreadNotifyGenerator implements NotifyBoxAnswerGenerator<DiscussionNewNotify> {

    @Override
    public BoxAnswer generate(DiscussionNewNotify notify) {
        final StringBuilder builder = new StringBuilder(Icons.THREAD).append(" *New Thread in your MR*");

        if (checkNotBlank(notify.getMessageTask())) {
            builder.append("\n -- -- -- merge request name -- -- --\n");
        } else {
            builder.append(Icons.HR);
        }

        builder
                .append(Icons.link(escapeMarkdown(notify.getMergeRequestName()), notify.getUrl()));

        if (checkNotBlank(notify.getMessageTask())) {
            builder.append("\n\n -- -- -- thread first message -- -- --\n")
                    .append("*").append(notify.getAuthorName()).append("*: ").append(escapeMarkdown(notify.getMessageTask()));
        }

        final List<Pair<String, String>> notes = notify.getNotes();
        if (checkNotEmpty(notes)) {
            builder.append("\n\n-- -- -- -- comments -- -- -- --\n")
                    .append(convertNotes(notes));
        }

        final String notifyMessage = builder.toString();
        return boxAnswer(
                notifyMessage,
                inlineKeyBoard(
                        simpleButton(Icons.VIEW, DELETE_MESSAGE),
                        urlButton(Icons.LINK, notify.getUrl()),
                        simpleButton(Icons.DISABLE_NOTIFY, "[" + BUTTON_ARG_DISABLE_NOTIFY_THREAD_ID + ":" + notify.getThreadId() + ";" + BUTTON_ARG_CONFIRMATION + ":" + BUTTON_VALUE_FALSE + "]")
                )
        );
    }

    private String convertNotes(List<Pair<String, String>> notes) {
        return notes.stream()
                .map(note -> "*" + note.getKey() + "*: " + note.getValue())
                .collect(Collectors.joining("\n"));
    }

    @Override
    public String getNotifyType() {
        return DiscussionNewNotify.TYPE;
    }

}
