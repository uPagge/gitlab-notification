package org.sadtech.bot.vcs.core.repository.impl;

import lombok.NonNull;
import org.sadtech.basic.database.repository.manager.AbstractSimpleManagerRepository;
import org.sadtech.bot.vcs.core.domain.entity.Chat;
import org.sadtech.bot.vcs.core.repository.ChatRepository;
import org.sadtech.bot.vcs.core.repository.jpa.ChatJpaRepository;
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
