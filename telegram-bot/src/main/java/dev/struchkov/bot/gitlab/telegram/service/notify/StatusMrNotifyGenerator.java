package dev.struchkov.bot.gitlab.telegram.service.notify;

import dev.struchkov.bot.gitlab.context.domain.notify.mergerequest.StatusMrNotify;
import dev.struchkov.bot.gitlab.context.utils.Icons;
import dev.struchkov.godfather.simple.domain.BoxAnswer;
import org.springframework.stereotype.Component;

import static dev.struchkov.bot.gitlab.telegram.utils.Const.BUTTON_ARG_CONFIRMATION;
import static dev.struchkov.bot.gitlab.telegram.utils.Const.BUTTON_ARG_DISABLE_NOTIFY_MR_ID;
import static dev.struchkov.bot.gitlab.telegram.utils.Const.BUTTON_VALUE_FALSE;
import static dev.struchkov.bot.gitlab.telegram.utils.UnitName.DELETE_MESSAGE;
import static dev.struchkov.godfather.main.domain.keyboard.button.SimpleButton.simpleButton;
import static dev.struchkov.godfather.main.domain.keyboard.simple.SimpleKeyBoardLine.simpleLine;
import static dev.struchkov.godfather.simple.domain.BoxAnswer.boxAnswer;
import static dev.struchkov.godfather.telegram.domain.keyboard.InlineKeyBoard.inlineKeyBoard;
import static dev.struchkov.godfather.telegram.domain.keyboard.button.UrlButton.urlButton;
import static dev.struchkov.haiti.utils.Strings.escapeMarkdown;

@Component
public class StatusMrNotifyGenerator implements NotifyBoxAnswerGenerator<StatusMrNotify> {

    @Override
    public BoxAnswer generate(StatusMrNotify notify) {

        final StringBuilder builder = new StringBuilder(Icons.PEN).append(" *MergeRequest status changed*")
                .append(Icons.HR)
                .append(escapeMarkdown(notify.getTitle())).append("\n\n")
                .append(notify.getOldStatus().name()).append(Icons.ARROW).append(notify.getNewStatus().name())
                .append(Icons.HR)
                .append(Icons.PROJECT).append(": ").append(escapeMarkdown(notify.getProjectName()));

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
        return StatusMrNotify.TYPE;
    }

}
