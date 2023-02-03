package dev.struchkov.bot.gitlab.telegram.service.notify;

import dev.struchkov.bot.gitlab.context.domain.notify.task.DiscussionNewNotify;
import dev.struchkov.bot.gitlab.context.utils.Icons;
import dev.struchkov.godfather.main.domain.BoxAnswer;
import dev.struchkov.haiti.utils.Pair;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

import static dev.struchkov.godfather.main.domain.BoxAnswer.boxAnswer;
import static dev.struchkov.haiti.utils.Checker.checkNotNull;
import static dev.struchkov.haiti.utils.Strings.escapeMarkdown;

@Component
public class DiscussionNewNotifyGenerator implements NotifyBoxAnswerGenerator<DiscussionNewNotify> {

    @Override
    public BoxAnswer generate(DiscussionNewNotify notify) {
        final StringBuilder builder = new StringBuilder(Icons.TASK).append(" [New discussion](").append(notify.getUrl()).append(")")
                .append(Icons.HR)
                .append(escapeMarkdown(notify.getMrName()))
                .append(Icons.HR)
                .append("*").append(notify.getAuthorName()).append("*: ").append(escapeMarkdown(notify.getMessageTask()));

        final List<Pair<String, String>> notes = notify.getNotes();
        if (checkNotNull(notes)) {
            builder.append("\n-- -- -- -- comments -- -- -- --\n")
                    .append(convertNotes(notes));
        }

        final String notifyMessage = builder.toString();
        return boxAnswer(notifyMessage);
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