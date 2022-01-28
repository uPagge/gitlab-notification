package dev.struchkov.bot.gitlab.telegram.service;

import dev.struchkov.bot.gitlab.context.domain.PersonInformation;
import dev.struchkov.bot.gitlab.context.domain.notify.Notify;
import dev.struchkov.bot.gitlab.context.service.AppSettingService;
import dev.struchkov.bot.gitlab.context.service.MessageSendService;
import dev.struchkov.godfather.context.domain.BoxAnswer;
import dev.struchkov.godfather.context.service.sender.Sending;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Отправляет сообщение в телеграмм.
 *
 * @author upagge 17.09.2020
 */
@Service
@RequiredArgsConstructor
public class MessageSendTelegramService implements MessageSendService {

    private final Sending sending;

    private final PersonInformation personInformation;
    private final AppSettingService settingService;

    @Override
    public void send(@NonNull Notify notify) {
        sending.send(personInformation.getTelegramId(), BoxAnswer.of(notify.generateMessage(settingService)));
    }

}
