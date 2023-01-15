package dev.struchkov.bot.gitlab.telegram.service.notify;

import dev.struchkov.bot.gitlab.context.domain.notify.mergerequest.NewPrNotify;
import dev.struchkov.bot.gitlab.context.utils.Icons;
import dev.struchkov.godfather.main.domain.BoxAnswer;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

import static dev.struchkov.bot.gitlab.context.utils.Icons.link;
import static dev.struchkov.godfather.main.domain.BoxAnswer.boxAnswer;
import static dev.struchkov.haiti.utils.Strings.escapeMarkdown;

@Component
public class NewPrNotifyGenerator implements NotifyBoxAnswerGenerator<NewPrNotify> {

    @Override
    public BoxAnswer generate(NewPrNotify notify) {
        final String labelText = notify.getLabels().stream()
                .map(label -> "#" + label)
                .collect(Collectors.joining(" "));


        final StringBuilder builder = new StringBuilder(Icons.FUN).append(" *New merge request for review | ").append(escapeMarkdown(notify.getProjectName())).append("*")
                .append(Icons.HR)
                .append(link(notify.getType(), notify.getUrl()));

        if (!labelText.isEmpty()) {
            builder.append("\n\n").append(labelText);
        }

        builder.append(Icons.HR)
                .append(Icons.TREE).append(": ").append(notify.getSourceBranch()).append(Icons.ARROW).append(notify.getTargetBranch()).append("\n")
                .append(Icons.AUTHOR).append(": ").append(notify.getAuthor());

        final String notifyMessage = builder.toString();
        return boxAnswer(notifyMessage);
    }

    @Override
    public String getNotifyType() {
        return NewPrNotify.TYPE;
    }

}
