package org.sadtech.bot.gitlab.telegram.service;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.sadtech.bot.gitlab.context.domain.PersonInformation;
import org.sadtech.bot.gitlab.context.domain.notify.Notify;
import org.sadtech.bot.gitlab.context.service.MessageSendService;
import org.sadtech.social.core.domain.BoxAnswer;
import org.sadtech.social.core.service.sender.Sending;
import org.springframework.stereotype.Service;

/**
 * // TODO: 17.09.2020 Добавить описание.
 *
 * @author upagge 17.09.2020
 */
@Service
@RequiredArgsConstructor
public class MessageSendTelegramService implements MessageSendService {

    private final Sending sending;

    private final PersonInformation personInformation;

    @Override
    public void send(@NonNull Notify notify) {
        sending.send(personInformation.getTelegramId(), BoxAnswer.of(notify.generateMessage()));
    }

}
