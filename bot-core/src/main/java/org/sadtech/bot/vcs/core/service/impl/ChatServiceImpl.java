package org.sadtech.bot.vcs.core.service.impl;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.sadtech.bot.vsc.bitbucketbot.context.repository.ChatRepository;
import org.sadtech.bot.vsc.bitbucketbot.context.service.ChatService;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * // TODO: 11.10.2020 Добавить описание.
 *
 * @author upagge 11.10.2020
 */
@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final ChatRepository chatRepository;

    @Override
    public Set<Long> getAllTelegramIdByKey(@NonNull Set<String> keys) {
        return chatRepository.findAllTelegramIdByKey(keys);
    }

}
