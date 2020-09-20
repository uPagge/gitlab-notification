package org.sadtech.bot.vcs.core.service;

import lombok.NonNull;
import org.sadtech.bot.vcs.core.domain.notify.Notify;

@FunctionalInterface
public interface MessageSendService {

    void send(@NonNull Notify notify);

}
