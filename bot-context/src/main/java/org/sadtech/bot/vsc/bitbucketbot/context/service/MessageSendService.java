package org.sadtech.bot.vsc.bitbucketbot.context.service;

import lombok.NonNull;
import org.sadtech.bot.vsc.bitbucketbot.context.domain.notify.Notify;

@FunctionalInterface
public interface MessageSendService {

    void send(@NonNull Notify notify);

}
