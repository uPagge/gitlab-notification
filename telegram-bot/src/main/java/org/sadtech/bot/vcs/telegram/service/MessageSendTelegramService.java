package org.sadtech.bot.vcs.telegram.service;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.sadtech.bot.vcs.core.domain.notify.Notify;
import org.sadtech.bot.vcs.core.service.MessageSendService;
import org.sadtech.bot.vcs.core.service.PersonService;
import org.sadtech.social.core.domain.BoxAnswer;
import org.sadtech.social.core.service.sender.Sending;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * // TODO: 17.09.2020 Добавить описание.
 *
 * @author upagge 17.09.2020
 */
@Service
@RequiredArgsConstructor
public class MessageSendTelegramService implements MessageSendService {

    private final Sending sending;

    private final PersonService personService;

    @Override
    public void send(@NonNull Notify notify) {
        final Set<Long> telegramIds = personService.getAllTelegramIdByLogin(notify.getLogins());
        telegramIds.forEach(
                telegramId -> sending.send(telegramId, BoxAnswer.of(notify.generateMessage()))
        );
    }

}
