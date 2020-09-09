package org.sadtech.bot.bitbucketbot.repository.impl;

import lombok.NonNull;
import org.sadtech.bot.bitbucketbot.domain.MessageSend;
import org.sadtech.bot.bitbucketbot.repository.MessageSendRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Repository
public class MessageSendRepositoryImpl implements MessageSendRepository {

    private List<MessageSend> messageSends = new ArrayList<>();
    private Long count = 1L;

    @Override
    public void add(@NonNull MessageSend messageSend) {
        messageSend.setId(count++);
        messageSends.add(messageSend);
    }

    @Override
    public List<MessageSend> getAll() {
        return new ArrayList<>(messageSends);
    }

    @Override
    public void deleteAll(@NonNull Collection<MessageSend> messageSends) {
        this.messageSends.removeAll(messageSends);
    }

}