package org.sadtech.bot.gitlab.core.service.impl;

import lombok.RequiredArgsConstructor;
import org.sadtech.bot.gitlab.context.domain.notify.Notify;
import org.sadtech.bot.gitlab.context.service.MessageSendService;
import org.sadtech.bot.gitlab.context.service.NotifyService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotifyServiceImpl implements NotifyService {

    private final MessageSendService messageSendService;

    @Override
    public <T extends Notify> void send(T notify) {
        messageSendService.send(notify);
    }

}
