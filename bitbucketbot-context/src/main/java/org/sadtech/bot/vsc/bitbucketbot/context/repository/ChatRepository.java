package org.sadtech.bot.vsc.bitbucketbot.context.repository;

import lombok.NonNull;
import org.sadtech.basic.context.repository.SimpleManagerRepository;
import org.sadtech.bot.vsc.bitbucketbot.context.domain.entity.Chat;

import java.util.Set;

/**
 * // TODO: 11.10.2020 Добавить описание.
 *
 * @author upagge 11.10.2020
 */
public interface ChatRepository extends SimpleManagerRepository<Chat, String> {

    Set<Long> findAllTelegramIdByKey(@NonNull Set<String> keys);

}
