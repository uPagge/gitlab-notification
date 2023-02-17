package dev.struchkov.bot.gitlab.telegram.service.notify;

import dev.struchkov.bot.gitlab.context.domain.notify.project.NewProjectNotify;
import dev.struchkov.bot.gitlab.context.utils.Icons;
import dev.struchkov.bot.gitlab.telegram.utils.Const;
import dev.struchkov.godfather.simple.domain.BoxAnswer;
import dev.struchkov.haiti.utils.Checker;
import dev.struchkov.haiti.utils.Strings;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static dev.struchkov.bot.gitlab.telegram.utils.UnitName.DELETE_MESSAGE;
import static dev.struchkov.godfather.main.domain.keyboard.button.SimpleButton.simpleButton;
import static dev.struchkov.godfather.main.domain.keyboard.simple.SimpleKeyBoardLine.simpleLine;
import static dev.struchkov.godfather.simple.domain.BoxAnswer.boxAnswer;
import static dev.struchkov.godfather.telegram.domain.keyboard.InlineKeyBoard.inlineKeyBoard;
import static dev.struchkov.godfather.telegram.domain.keyboard.button.UrlButton.urlButton;
import static dev.struchkov.haiti.utils.Strings.escapeMarkdown;

@Component
public class NewProjectNotifyGenerator implements NotifyBoxAnswerGenerator<NewProjectNotify> {

    @Override
    public BoxAnswer generate(NewProjectNotify notify) {
        final Optional<String> optDescription = Optional.ofNullable(notify.getProjectDescription())
                .filter(Checker::checkNotBlank)
                .map(Strings::escapeMarkdown);

        final StringBuilder builder = new StringBuilder(Icons.FUN).append(" *New project*")
                .append(Icons.HR)
                .append(escapeMarkdown(notify.getProjectName()))
                .append(Icons.HR);

        if (optDescription.isPresent()) {
            final String description = optDescription.get();
            builder.append(escapeMarkdown(description))
                    .append(Icons.HR);
        }

        builder.append(Icons.AUTHOR).append(": ").append(escapeMarkdown(notify.getAuthorName()));

        final String notifyMessage = builder.toString();

        return boxAnswer(
                notifyMessage,
                inlineKeyBoard(
                        simpleLine(urlButton(Icons.LINK, notify.getProjectUrl())),
                        simpleLine(
                                simpleButton(Icons.NOTIFY, "[" + Const.BUTTON_ARG_ENABLE_NOTIFY_PROJECT_ID + ":" + notify.getProjectId() + "]"),
                                simpleButton(Icons.DISABLE_NOTIFY, DELETE_MESSAGE)
                        )
                )
        );
    }

    @Override
    public String getNotifyType() {
        return NewProjectNotify.TYPE;
    }

}
