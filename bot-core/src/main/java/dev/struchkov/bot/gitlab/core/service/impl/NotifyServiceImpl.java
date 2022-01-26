package dev.struchkov.bot.gitlab.core.service.impl;

import dev.struchkov.bot.gitlab.context.domain.notify.Notify;
import dev.struchkov.bot.gitlab.context.service.MessageSendService;
import dev.struchkov.bot.gitlab.context.service.NotifyService;
import lombok.RequiredArgsConstructor;
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
