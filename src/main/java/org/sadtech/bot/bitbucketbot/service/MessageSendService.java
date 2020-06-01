package org.sadtech.bot.bitbucketbot.service;

import lombok.NonNull;
import org.sadtech.bot.bitbucketbot.domain.MessageSend;

import java.util.List;

public interface MessageSendService {

    void add(@NonNull MessageSend messageSend);

    List<MessageSend> getPushMessage();

}
