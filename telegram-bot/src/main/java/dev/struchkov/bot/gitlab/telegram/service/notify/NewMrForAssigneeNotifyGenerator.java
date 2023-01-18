package dev.struchkov.bot.gitlab.telegram.service.notify;

import dev.struchkov.bot.gitlab.context.domain.notify.mergerequest.NewMrForAssignee;
import dev.struchkov.bot.gitlab.context.utils.Icons;
import dev.struchkov.godfather.main.domain.BoxAnswer;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

import static dev.struchkov.bot.gitlab.telegram.utils.Const.BUTTON_ARG_CONFIRMATION;
import static dev.struchkov.bot.gitlab.telegram.utils.Const.BUTTON_ARG_DISABLE_NOTIFY_MR_ID;
import static dev.struchkov.bot.gitlab.telegram.utils.Const.BUTTON_VALUE_FALSE;
import static dev.struchkov.bot.gitlab.telegram.utils.UnitName.DELETE_MESSAGE;
import static dev.struchkov.godfather.main.domain.BoxAnswer.boxAnswer;
import static dev.struchkov.godfather.main.domain.keyboard.button.SimpleButton.simpleButton;
import static dev.struchkov.godfather.main.domain.keyboard.simple.SimpleKeyBoardLine.simpleLine;
import static dev.struchkov.godfather.telegram.domain.keyboard.InlineKeyBoard.inlineKeyBoard;
import static dev.struchkov.godfather.telegram.domain.keyboard.button.UrlButton.urlButton;
import static dev.struchkov.haiti.utils.Checker.checkNotEmpty;
import static dev.struchkov.haiti.utils.Checker.checkNotNull;
import static dev.struchkov.haiti.utils.Strings.escapeMarkdown;

@Component
public class NewMrForAssigneeNotifyGenerator implements NotifyBoxAnswerGenerator<NewMrForAssignee> {

    @Override
    public BoxAnswer generate(NewMrForAssignee notify) {
        final String labelText = notify.getLabels().stream()
                .map(label -> "#" + label)
                .collect(Collectors.joining(" "));

        final StringBuilder builder = new StringBuilder(Icons.ASSIGNEE).append(" *You have become responsible*")
                .append(Icons.HR)
                .append(notify.getTitle());

        if (!labelText.isEmpty()) {
            builder.append("\n\n").append(labelText);
        }

        builder.append(Icons.HR);

        if (checkNotNull(notify.getProjectName())) {
            builder.append(Icons.PROJECT).append(": ").append(escapeMarkdown(notify.getProjectName())).append("\n");
        }

        builder
                .append(Icons.TREE).append(": ").append(notify.getSourceBranch()).append(Icons.ARROW).append(notify.getTargetBranch()).append("\n")
                .append(Icons.AUTHOR).append(": ").append(notify.getAuthor()).append("\n");

        final List<String> reviewers = notify.getReviewers();
        if (checkNotEmpty(reviewers)) {
            builder.append(Icons.REVIEWER).append(": ").append(String.join(", ", reviewers)).append("\n");
        }

        if (checkNotNull(notify.getOldAssigneeName()) && checkNotNull(notify.getNewAssigneeName())) {
            builder.append(Icons.ASSIGNEE).append(": ").append(notify.getOldAssigneeName()).append(Icons.ARROW).append(notify.getNewAssigneeName()).append("\n");
        }

        final String notifyMessage = builder.toString();

        return boxAnswer(
                notifyMessage,
                inlineKeyBoard(
                        simpleLine(
                                simpleButton(Icons.VIEW, DELETE_MESSAGE),
                                urlButton(Icons.LINK, notify.getUrl()),
                                simpleButton(Icons.DISABLE_NOTIFY, "[" + BUTTON_ARG_DISABLE_NOTIFY_MR_ID + ":" + notify.getMrId() + ";" + BUTTON_ARG_CONFIRMATION + ":" + BUTTON_VALUE_FALSE + "]")
                        )
                )
        );
    }

    @Override
    public String getNotifyType() {
        return NewMrForAssignee.TYPE;
    }

}
