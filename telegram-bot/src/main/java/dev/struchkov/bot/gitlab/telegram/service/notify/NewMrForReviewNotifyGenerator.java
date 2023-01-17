package dev.struchkov.bot.gitlab.telegram.service.notify;

import dev.struchkov.bot.gitlab.context.domain.notify.mergerequest.NewMrForReview;
import dev.struchkov.bot.gitlab.context.utils.Icons;
import dev.struchkov.godfather.main.domain.BoxAnswer;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

import static dev.struchkov.bot.gitlab.context.utils.Icons.link;
import static dev.struchkov.godfather.main.domain.BoxAnswer.boxAnswer;
import static dev.struchkov.godfather.main.domain.keyboard.button.SimpleButton.simpleButton;
import static dev.struchkov.godfather.main.domain.keyboard.simple.SimpleKeyBoardLine.simpleLine;
import static dev.struchkov.godfather.telegram.domain.keyboard.InlineKeyBoard.inlineKeyBoard;
import static dev.struchkov.godfather.telegram.domain.keyboard.button.UrlButton.urlButton;
import static dev.struchkov.haiti.utils.Checker.checkNotNull;
import static dev.struchkov.haiti.utils.Strings.escapeMarkdown;

@Component
public class NewMrForReviewNotifyGenerator implements NotifyBoxAnswerGenerator<NewMrForReview> {

    @Override
    public BoxAnswer generate(NewMrForReview notify) {
        final String labelText = notify.getLabels().stream()
                .map(label -> "#" + label)
                .collect(Collectors.joining(" "));


        final StringBuilder builder = new StringBuilder(Icons.FUN).append(" *New merge request for review*")
                .append(Icons.HR)
                .append(link(notify.getType(), notify.getUrl()));

        if (!labelText.isEmpty()) {
            builder.append("\n\n").append(labelText);
        }

        builder.append(Icons.HR);

        if (checkNotNull(notify.getProjectName())) {
            builder.append("Project").append(": ").append(escapeMarkdown(notify.getProjectName()));
        }

        builder
                .append(Icons.TREE).append(": ").append(notify.getSourceBranch()).append(Icons.ARROW).append(notify.getTargetBranch()).append("\n")
                .append(Icons.AUTHOR).append(": ").append(notify.getAuthor());

        if (checkNotNull(notify.getAssignee())) {
            builder.append(Icons.ASSIGNEE).append(": ").append(notify.getAssignee());
        }

        final String notifyMessage = builder.toString();
        return boxAnswer(
                notifyMessage,
                inlineKeyBoard(
                        simpleLine(
                                simpleButton(Icons.VIEW, "DELETE_MESSAGE"),
                                urlButton(Icons.LINK, notify.getUrl())
                        )
                )
        );
    }

    @Override
    public String getNotifyType() {
        return NewMrForReview.TYPE;
    }

}
