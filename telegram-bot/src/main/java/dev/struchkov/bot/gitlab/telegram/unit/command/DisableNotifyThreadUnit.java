package dev.struchkov.bot.gitlab.telegram.unit.command;

import dev.struchkov.bot.gitlab.context.domain.PersonInformation;
import dev.struchkov.bot.gitlab.context.service.AppSettingService;
import dev.struchkov.bot.gitlab.context.service.DiscussionService;
import dev.struchkov.bot.gitlab.context.utils.Icons;
import dev.struchkov.godfather.main.domain.annotation.Unit;
import dev.struchkov.godfather.main.domain.content.Mail;
import dev.struchkov.godfather.simple.core.unit.AnswerText;
import dev.struchkov.godfather.telegram.domain.attachment.ButtonClickAttachment;
import dev.struchkov.godfather.telegram.domain.attachment.ButtonClickAttachment.Arg;
import dev.struchkov.godfather.telegram.main.core.util.Attachments;
import dev.struchkov.godfather.telegram.simple.context.service.TelegramSending;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static dev.struchkov.bot.gitlab.telegram.utils.Const.BUTTON_ARG_CONFIRMATION;
import static dev.struchkov.bot.gitlab.telegram.utils.Const.BUTTON_ARG_DISABLE_NOTIFY_THREAD_ID;
import static dev.struchkov.bot.gitlab.telegram.utils.Const.BUTTON_VALUE_TRUE;
import static dev.struchkov.bot.gitlab.telegram.utils.UnitName.DELETE_MESSAGE;
import static dev.struchkov.bot.gitlab.telegram.utils.UnitName.DISABLE_NOTIFY_THREAD;
import static dev.struchkov.godfather.main.domain.BoxAnswer.replaceBoxAnswer;
import static dev.struchkov.godfather.main.domain.keyboard.button.SimpleButton.simpleButton;
import static dev.struchkov.godfather.main.domain.keyboard.simple.SimpleKeyBoardLine.simpleLine;
import static dev.struchkov.godfather.telegram.domain.keyboard.InlineKeyBoard.inlineKeyBoard;

@Component
@RequiredArgsConstructor
public class DisableNotifyThreadUnit {

    public static final String WARNING_ABOUT_DISABLE_NOTIFY = Icons.DISABLE_NOTIFY + """
             *Disabling notifications*
                                            
            Are you sure you want to stop receiving notifications of new replies to this thread?
            """;
    public static final String SUCCESSFULLY_DISABLED = "Notifications successfully disabled for this thread";

    private final DiscussionService discussionService;
    private final PersonInformation personInformation;
    private final AppSettingService settingService;
    private final TelegramSending telegramSending;

    private final ScheduledExecutorService scheduledExecutorService;

    @Unit(value = DISABLE_NOTIFY_THREAD, global = true)
    public AnswerText<Mail> disableNotifyThread() {
        return AnswerText.<Mail>builder()
                .triggerCheck(mail -> {
                    final boolean isDisableButtonClick = Attachments.findFirstButtonClick(mail.getAttachments())
                            .flatMap(buttonClick -> buttonClick.getArgByType(BUTTON_ARG_DISABLE_NOTIFY_THREAD_ID))
                            .isPresent();
                    if (isDisableButtonClick) {
                        final boolean isAccess = personInformation.getTelegramId().equals(mail.getPersonId());
                        final boolean isFirstStart = settingService.isFirstStart();
                        return isAccess && !isFirstStart;
                    }
                    return false;
                })
                .answer(mail -> {
                    final ButtonClickAttachment clickButton = Attachments.findFirstButtonClick(mail.getAttachments()).orElseThrow();
                    final boolean confirmation = clickButton.getArgByType(BUTTON_ARG_CONFIRMATION)
                            .map(Arg::getValue)
                            .map(BUTTON_VALUE_TRUE::equals)
                            .orElseThrow();
                    final String discussionId = clickButton.getArgByType(BUTTON_ARG_DISABLE_NOTIFY_THREAD_ID)
                            .map(Arg::getValue)
                            .map(String::valueOf)
                            .orElseThrow();

                    if (confirmation) {
                        discussionService.notification(false, discussionId);
                        scheduledExecutorService.schedule(() -> telegramSending.deleteMessage(mail.getPersonId(), clickButton.getMessageId()), 5, TimeUnit.SECONDS);
                        return replaceBoxAnswer(SUCCESSFULLY_DISABLED);
                    } else {
                        return replaceBoxAnswer(
                                WARNING_ABOUT_DISABLE_NOTIFY,
                                inlineKeyBoard(
                                        simpleLine(
                                                simpleButton(Icons.YES, "[" + BUTTON_ARG_DISABLE_NOTIFY_THREAD_ID + ":" + discussionId + ";" + BUTTON_ARG_CONFIRMATION + ":" + BUTTON_VALUE_TRUE + "]"),
                                                simpleButton(Icons.NO, DELETE_MESSAGE)
                                        )
                                )
                        );
                    }
                })
                .build();
    }

}
