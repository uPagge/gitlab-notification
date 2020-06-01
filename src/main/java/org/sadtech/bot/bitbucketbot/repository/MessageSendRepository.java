package org.sadtech.bot.bitbucketbot.repository;

import lombok.NonNull;
import org.sadtech.bot.bitbucketbot.domain.MessageSend;

import java.util.Collection;
import java.util.List;

public interface MessageSendRepository {

    void add(@NonNull MessageSend messageSend);

    List<MessageSend> getAll();

    void deleteAll(@NonNull Collection<MessageSend> messageSends);

}
