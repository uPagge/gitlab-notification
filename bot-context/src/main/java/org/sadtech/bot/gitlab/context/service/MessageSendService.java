package org.sadtech.bot.gitlab.context.service;

import lombok.NonNull;
import org.sadtech.bot.gitlab.context.domain.notify.Notify;

@FunctionalInterface
public interface MessageSendService {

    void send(@NonNull Notify notify);

}
