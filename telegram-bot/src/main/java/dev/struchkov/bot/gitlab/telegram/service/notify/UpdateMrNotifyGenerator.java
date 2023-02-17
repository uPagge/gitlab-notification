package dev.struchkov.bot.gitlab.telegram.service.notify;

import dev.struchkov.bot.gitlab.context.domain.notify.mergerequest.UpdateMrNotify;
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
import static dev.struchkov.haiti.utils.Checker.checkNotNull;
import static dev.struchkov.haiti.utils.Strings.escapeMarkdown;

@Component
public class UpdateMrNotifyGenerator implements NotifyBoxAnswerGenerator<UpdateMrNotify> {

    @Override
    public BoxAnswer generate(UpdateMrNotify notify) {

        final StringBuilder builder = new StringBuilder(Icons.UPDATE).append(" *MergeRequest update*")
                .append(Icons.HR)
                .append(notify.getTitle());

        if (notify.getAllTasks() > 0) {
            builder.append(Icons.HR)
                    .append("All tasks: ").append(notify.getAllResolvedTasks()).append("/").append(notify.getAllTasks());

            if (notify.getPersonTasks() > 0) {
                builder.append("\nYour tasks: ").append(notify.getPersonResolvedTasks()).append("/").append(notify.getPersonTasks());
            }
        }

        builder.append(Icons.HR);

        if (checkNotNull(notify.getProjectName())) {
            builder.append(Icons.PROJECT).append(": ").append(escapeMarkdown(notify.getProjectName())).append("\n");
        }

        builder
                .append(Icons.AUTHOR).append(": ").append(notify.getAuthor());

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
        return UpdateMrNotify.TYPE;
    }

}
