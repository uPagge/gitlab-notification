package org.sadtech.bot.vcs.bitbucketbot.data.impl;

import lombok.NonNull;
import org.sadtech.basic.database.repository.manager.AbstractSimpleManagerRepository;
import org.sadtech.bot.vcs.bitbucketbot.data.jpa.ChatJpaRepository;
import org.sadtech.bot.vsc.bitbucketbot.context.domain.entity.Chat;
import org.sadtech.bot.vsc.bitbucketbot.context.repository.ChatRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

/**
 * // TODO: 11.10.2020 Добавить описание.
 *
 * @author upagge 11.10.2020
 */
@Repository
public class ChatRepositoryImpl extends AbstractSimpleManagerRepository<Chat, String> implements ChatRepository {

    private final ChatJpaRepository jpaRepository;

    public ChatRepositoryImpl(ChatJpaRepository jpaRepository) {
        super(jpaRepository);
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Set<Long> findAllTelegramIdByKey(@NonNull Set<String> keys) {
        return jpaRepository.findAllTelegramIdByKey(keys);
    }

}
