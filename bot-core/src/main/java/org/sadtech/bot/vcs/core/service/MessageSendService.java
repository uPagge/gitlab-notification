package org.sadtech.bot.vcs.core.service;

import lombok.NonNull;
import org.sadtech.bot.vcs.core.domain.MessageSend;

public interface MessageSendService {

    void add(@NonNull MessageSend messageSend);

}
