package dev.struchkov.bot.gitlab.telegram.service.notify;

import dev.struchkov.bot.gitlab.context.domain.notify.issue.NewIssueNotify;
import dev.struchkov.bot.gitlab.context.utils.Icons;
import dev.struchkov.godfather.simple.domain.BoxAnswer;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

import static dev.struchkov.bot.gitlab.context.utils.Icons.link;
import static dev.struchkov.godfather.simple.domain.BoxAnswer.boxAnswer;
import static dev.struchkov.haiti.utils.Strings.escapeMarkdown;

/**
 * @author Dmitry Sheyko 24.01.2023
 */
@Component
public class NewIssueNotifyGenerator implements NotifyBoxAnswerGenerator<NewIssueNotify> {

    @Override
    public BoxAnswer generate(NewIssueNotify notify) {
        final String labelText = notify.getLabels().stream()
                .map(label -> "#" + label)
                .collect(Collectors.joining(" "));

        final StringBuilder builder = new StringBuilder(Icons.FUN)
                .append(String.format(" *New %s assigned to you | ", notify.getIssueType()))
                .append(escapeMarkdown(notify.getProjectName())).append("*")
                .append(Icons.HR)
                .append(link(notify.getType(), notify.getUrl()));

        if (!labelText.isEmpty()) {
            builder.append("\n\n").append(labelText);
        }

        builder.append(Icons.HR)
                .append(Icons.BELL).append(": ").append(notify.getTitle()).append("\n")
                .append(Icons.AUTHOR).append(": ").append(notify.getAuthor());

        final String notifyMessage = builder.toString();
        return boxAnswer(notifyMessage);
    }

    @Override
    public String getNotifyType() {
        return NewIssueNotify.TYPE;
    }

}