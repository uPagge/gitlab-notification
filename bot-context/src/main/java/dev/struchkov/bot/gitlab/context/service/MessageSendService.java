package dev.struchkov.bot.gitlab.context.service;

import lombok.NonNull;
import dev.struchkov.bot.gitlab.context.domain.notify.Notify;

@FunctionalInterface
public interface MessageSendService {

    void send(@NonNull Notify notify);

}
