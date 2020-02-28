package com.tsc.bitbucketbot.service;

import com.tsc.bitbucketbot.domain.MessageSend;
import lombok.NonNull;

import java.util.List;

public interface MessageSendService {

    void add(@NonNull MessageSend messageSend);

    List<MessageSend> getPushMessage();

}
