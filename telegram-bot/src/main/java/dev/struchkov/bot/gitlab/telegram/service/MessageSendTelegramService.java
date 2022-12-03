package dev.struchkov.bot.gitlab.telegram.service;

import dev.struchkov.bot.gitlab.context.domain.PersonInformation;
import dev.struchkov.bot.gitlab.context.domain.notify.Notify;
import dev.struchkov.bot.gitlab.context.service.MessageSendService;
import dev.struchkov.godfather.telegram.simple.context.service.TelegramSending;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static dev.struchkov.godfather.main.domain.BoxAnswer.boxAnswer;

/**
 * Отправляет сообщение в телеграмм.
 *
 * @author upagge 17.09.2020
 */
@Service
@RequiredArgsConstructor
public class MessageSendTelegramService implements MessageSendService {

    private final TelegramSending sending;

    private final PersonInformation personInformation;

    @Override
    public void send(@NonNull Notify notify) {
        sending.send(personInformation.getTelegramId(), boxAnswer(notify.generateMessage()));
    }

}
