package dev.struchkov.bot.gitlab.telegram.unit.command;

import dev.struchkov.bot.gitlab.context.domain.PersonInformation;
import dev.struchkov.bot.gitlab.context.service.AppSettingService;
import dev.struchkov.godfather.main.domain.annotation.Unit;
import dev.struchkov.godfather.main.domain.content.Mail;
import dev.struchkov.godfather.simple.domain.unit.AnswerText;
import dev.struchkov.godfather.telegram.domain.attachment.ButtonClickAttachment;
import dev.struchkov.godfather.telegram.main.core.util.Attachments;
import dev.struchkov.godfather.telegram.simple.context.service.TelegramSending;
import dev.struchkov.godfather.telegram.starter.UnitConfiguration;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static dev.struchkov.bot.gitlab.telegram.utils.UnitName.DELETE_MESSAGE;

@Component
@RequiredArgsConstructor
public class DeleteMessageUnit implements UnitConfiguration {

    private final TelegramSending telegramSending;
    private final PersonInformation personInformation;
    private final AppSettingService settingService;

    @Unit(value = DELETE_MESSAGE, global = true)
    public AnswerText<Mail> deleteMessage() {
        return AnswerText.<Mail>builder()
                .triggerCheck(mail -> {
                    final boolean isAccess = personInformation.getTelegramId().equals(mail.getPersonId());
                    if (isAccess) {
                        final boolean isFirstStart = settingService.isFirstStart();
                        if (!isFirstStart) {
                            final Optional<ButtonClickAttachment> optButtonClick = Attachments.findFirstButtonClick(mail.getAttachments());
                            if (optButtonClick.isPresent()) {
                                final ButtonClickAttachment buttonClick = optButtonClick.get();
                                final String rawData = buttonClick.getRawCallBackData();
                                return rawData.equals(DELETE_MESSAGE);
                            }
                        }
                    }
                    return false;
                })
                .answer(mail -> {
                    final ButtonClickAttachment clickButton = Attachments.findFirstButtonClick(mail.getAttachments()).orElseThrow();
                    telegramSending.deleteMessage(mail.getPersonId(), clickButton.getMessageId());
                })
                .build();
    }

}
