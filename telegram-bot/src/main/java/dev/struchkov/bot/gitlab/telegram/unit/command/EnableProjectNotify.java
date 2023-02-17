package dev.struchkov.bot.gitlab.telegram.unit.command;

import dev.struchkov.bot.gitlab.context.domain.PersonInformation;
import dev.struchkov.bot.gitlab.context.service.AppSettingService;
import dev.struchkov.bot.gitlab.context.service.MergeRequestsService;
import dev.struchkov.bot.gitlab.context.service.ProjectService;
import dev.struchkov.bot.gitlab.context.utils.Icons;
import dev.struchkov.bot.gitlab.telegram.utils.UnitName;
import dev.struchkov.godfather.main.domain.annotation.Unit;
import dev.struchkov.godfather.main.domain.content.Mail;
import dev.struchkov.godfather.simple.core.unit.AnswerText;
import dev.struchkov.godfather.telegram.domain.attachment.ButtonClickAttachment;
import dev.struchkov.godfather.telegram.domain.attachment.ButtonClickAttachment.Arg;
import dev.struchkov.godfather.telegram.main.core.util.Attachments;
import dev.struchkov.godfather.telegram.simple.context.service.TelegramSending;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static dev.struchkov.bot.gitlab.telegram.utils.Const.BUTTON_ARG_ENABLE_NOTIFY_PROJECT_ID;
import static dev.struchkov.godfather.simple.domain.BoxAnswer.replaceBoxAnswer;

@Component
@RequiredArgsConstructor
public class EnableProjectNotify {

    private final ProjectService projectService;
    private final MergeRequestsService mergeRequestsService;

    private final AppSettingService settingService;
    private final PersonInformation personInformation;

    private final TelegramSending sending;
    private final ScheduledExecutorService scheduledExecutorService;

    @Unit(value = UnitName.ENABLE_NOTIFY_PROJECT, global = true)
    public AnswerText<Mail> enableNotifyProject() {
        return AnswerText.<Mail>builder()
                .triggerCheck(mail -> {
                    final boolean isAccess = personInformation.getTelegramId().equals(mail.getPersonId());
                    if (isAccess) {
                        final boolean isFirstStart = settingService.isFirstStart();
                        if (!isFirstStart) {
                            final Optional<ButtonClickAttachment> optButtonClick = Attachments.findFirstButtonClick(mail.getAttachments());
                            if (optButtonClick.isPresent()) {
                                final ButtonClickAttachment buttonClick = optButtonClick.get();
                                return buttonClick.getArgByType(BUTTON_ARG_ENABLE_NOTIFY_PROJECT_ID).isPresent();
                            }
                        }
                    }
                    return false;
                }).answer(
                        mail -> {
                            final ButtonClickAttachment buttonClick = Attachments.findFirstButtonClick(mail.getAttachments()).orElseThrow();
                            final Arg arg = buttonClick.getArgByType(BUTTON_ARG_ENABLE_NOTIFY_PROJECT_ID).orElseThrow();
                            final long projectId = Long.parseLong(arg.getValue());
                            final Set<Long> setProjectId = Set.of(projectId);
                            projectService.processing(true, setProjectId);
                            projectService.notification(true, setProjectId);
                            mergeRequestsService.notificationByProjectId(true, setProjectId);
                            return replaceBoxAnswer(mail.getId(), Icons.GOOD + " you will now receive notifications!");
                        }
                )
                .callBack(
                        sentBox -> scheduledExecutorService.schedule(() -> sending.deleteMessage(sentBox.getPersonId(), sentBox.getMessageId()), 5, TimeUnit.SECONDS)
                )
                .build();
    }

}
