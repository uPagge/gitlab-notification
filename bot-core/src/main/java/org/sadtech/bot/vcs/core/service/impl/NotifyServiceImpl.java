package org.sadtech.bot.vcs.core.service.impl;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.sadtech.bot.vsc.bitbucketbot.context.domain.EntityType;
import org.sadtech.bot.vsc.bitbucketbot.context.domain.entity.NotifySetting;
import org.sadtech.bot.vsc.bitbucketbot.context.domain.notify.Notify;
import org.sadtech.bot.vsc.bitbucketbot.context.repository.NotifySettingRepository;
import org.sadtech.bot.vsc.bitbucketbot.context.service.MessageSendService;
import org.sadtech.bot.vsc.bitbucketbot.context.service.NotifyService;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class NotifyServiceImpl implements NotifyService {

    private final NotifySettingRepository settingRepository;

    private final MessageSendService messageSendService;

    @Override
    public <T extends Notify> void send(T notify) {
        if (EntityType.PERSON.equals(notify.getEntityType())) {
            final Set<String> recipientLogins = settingRepository.isNotification(notify.getRecipients());
            notify.setRecipients(recipientLogins);
        }
        messageSendService.send(notify);
    }

    @Override
    public void saveSettings(@NonNull NotifySetting setting) {
        settingRepository.save(setting);
    }

    @Override
    public Optional<NotifySetting> getSetting(@NonNull String login) {
        return settingRepository.findById(login);
    }

}
