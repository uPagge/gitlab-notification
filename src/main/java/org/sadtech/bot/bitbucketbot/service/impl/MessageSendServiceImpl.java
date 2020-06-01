package org.sadtech.bot.bitbucketbot.service.impl;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.sadtech.bot.bitbucketbot.domain.MessageSend;
import org.sadtech.bot.bitbucketbot.repository.MessageSendRepository;
import org.sadtech.bot.bitbucketbot.service.MessageSendService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageSendServiceImpl implements MessageSendService {

    private final MessageSendRepository messageSendRepository;

    @Override
    public void add(@NonNull MessageSend messageSend) {
        messageSend.setMessage(
                messageSend.getMessage().replace("localhost", "192.168.236.164")
        );
        messageSendRepository.add(messageSend);
    }

    @Override
    public List<MessageSend> getPushMessage() {
        List<MessageSend> newMessages = messageSendRepository.getAll();
        messageSendRepository.deleteAll(newMessages);
        return newMessages;
    }

}
