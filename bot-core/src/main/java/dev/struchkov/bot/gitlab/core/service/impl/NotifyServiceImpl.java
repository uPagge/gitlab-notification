package dev.struchkov.bot.gitlab.core.service.impl;

import dev.struchkov.bot.gitlab.context.domain.notify.Notify;
import dev.struchkov.bot.gitlab.context.service.AppSettingService;
import dev.struchkov.bot.gitlab.context.service.MessageSendService;
import dev.struchkov.bot.gitlab.context.service.NotifyService;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Service
public class NotifyServiceImpl implements NotifyService {

    private final MessageSendService messageSendService;
    private final AppSettingService settingService;

    public NotifyServiceImpl(
            @Lazy MessageSendService messageSendService,
            AppSettingService settingService
    ) {
        this.messageSendService = messageSendService;
        this.settingService = settingService;
    }

    @Override
    public <T extends Notify> void send(T notify) {
        if (settingService.isEnableAllNotify()) {
            messageSendService.send(notify);
        }
    }

}
