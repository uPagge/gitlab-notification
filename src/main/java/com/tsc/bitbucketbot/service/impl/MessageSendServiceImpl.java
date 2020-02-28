package com.tsc.bitbucketbot.service.impl;

import com.tsc.bitbucketbot.domain.MessageSend;
import com.tsc.bitbucketbot.repository.MessageSendRepository;
import com.tsc.bitbucketbot.service.MessageSendService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageSendServiceImpl implements MessageSendService {

    private final MessageSendRepository messageSendRepository;

    @Override
    public void add(@NonNull MessageSend messageSend) {
        messageSendRepository.add(messageSend);
    }

    @Override
    public List<MessageSend> getPushMessage() {
        List<MessageSend> newMessages = messageSendRepository.getAll();
        messageSendRepository.deleteAll(newMessages);
        return newMessages;
    }

}
