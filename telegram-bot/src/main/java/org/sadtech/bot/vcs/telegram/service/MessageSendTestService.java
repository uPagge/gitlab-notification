package org.sadtech.bot.vcs.telegram.service;

import lombok.NonNull;
import org.sadtech.bot.vcs.core.domain.MessageSend;
import org.sadtech.bot.vcs.core.service.MessageSendService;
import org.springframework.context.annotation.Profile;

/**
 * // TODO: 17.09.2020 Добавить описание.
 *
 * @author upagge 17.09.2020
 */
//@Service
@Profile("dev")
public class MessageSendTestService implements MessageSendService {

    @Override
    public void add(@NonNull MessageSend messageSend) {
        System.out.println(messageSend);
    }

}
