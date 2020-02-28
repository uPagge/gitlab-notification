package com.tsc.bitbucketbot.repository;

import com.tsc.bitbucketbot.domain.MessageSend;
import lombok.NonNull;

import java.util.Collection;
import java.util.List;

public interface MessageSendRepository {

    void add(@NonNull MessageSend messageSend);

    List<MessageSend> getAll();

    void deleteAll(@NonNull Collection<MessageSend> messageSends);

}
