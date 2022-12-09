package dev.struchkov.bot.gitlab.context.domain.notify.task;

import dev.struchkov.bot.gitlab.context.utils.Smile;
import dev.struchkov.haiti.utils.Pair;
import lombok.Builder;
import lombok.Getter;
import lombok.Singular;

import java.util.List;
import java.util.stream.Collectors;

import static dev.struchkov.haiti.utils.Checker.checkNotNull;
import static dev.struchkov.haiti.utils.Strings.escapeMarkdown;

/**
 * @author upagge 10.09.2020
 */
@Getter
public class DiscussionNewNotify extends TaskNotify {

    private final String mrName;
    private final List<Pair<String, String>> notes;

    @Builder
    public DiscussionNewNotify(
            String mrName,
            String authorName,
            String url,
            String discussionMessage,
            @Singular List<Pair<String, String>> notes
    ) {
        super(authorName, url, discussionMessage);
        this.mrName = mrName;
        this.notes = notes;
    }

    @Override
    public String generateMessage() {
        final StringBuilder builder = new StringBuilder(Smile.TASK.getValue()).append(" [New discussion](").append(url).append(")")
                .append(Smile.HR.getValue())
                .append(escapeMarkdown(mrName))
                .append(Smile.HR.getValue())
                .append("*").append(authorName).append("*: ").append(escapeMarkdown(messageTask));

        if (checkNotNull(notes)) {
            builder.append("\n-- -- -- -- comments -- -- -- --\n")
                    .append(convertNotes(notes));
        }

        return builder.toString();
    }

    private String convertNotes(List<Pair<String, String>> notes) {
        return notes.stream()
                .map(note -> "*" + note.getKey() + "*: " + note.getValue())
                .collect(Collectors.joining("\n"));
    }

}
