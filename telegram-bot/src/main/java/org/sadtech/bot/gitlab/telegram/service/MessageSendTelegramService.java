package org.sadtech.bot.gitlab.telegram.service;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.sadtech.bot.gitlab.context.domain.notify.Notify;
import org.sadtech.bot.gitlab.context.exception.NotFoundException;
import org.sadtech.bot.gitlab.context.service.ChatService;
import org.sadtech.bot.gitlab.context.service.MessageSendService;
import org.sadtech.bot.gitlab.context.service.PersonService;
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
    private final ChatService chatService;

    @Override
    public void send(@NonNull Notify notify) {
        final Set<Long> telegramIds = getTelegramIds(notify);
        telegramIds.forEach(
                telegramId -> sending.send(telegramId, BoxAnswer.of(notify.generateMessage()))
        );
    }

    private Set<Long> getTelegramIds(Notify notify) {
        switch (notify.getEntityType()) {
            case PERSON:
                return personService.getAllTelegramIdByLogin(notify.getRecipients());
            case CHAT:
                return chatService.getAllTelegramIdByKey(notify.getRecipients());
            default:
                throw new NotFoundException("Отправка сообщения этому типу не возможна");
        }
    }

}
