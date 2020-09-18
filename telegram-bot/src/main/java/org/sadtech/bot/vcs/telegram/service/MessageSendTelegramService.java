package org.sadtech.bot.vcs.telegram.service;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.sadtech.bot.vcs.core.domain.MessageSend;
import org.sadtech.bot.vcs.core.service.MessageSendService;
import org.sadtech.social.core.domain.BoxAnswer;
import org.sadtech.social.core.service.sender.Sending;
import org.springframework.stereotype.Service;

/**
 * // TODO: 17.09.2020 Добавить описание.
 *
 * @author upagge 17.09.2020
 */
//@Profile("prod")
@Service
@RequiredArgsConstructor
public class MessageSendTelegramService implements MessageSendService {

    private final Sending sending;

    @Override
    public void add(@NonNull MessageSend messageSend) {
        sending.send(messageSend.getTelegramId(), BoxAnswer.of(messageSend.getMessage()));
    }

}
