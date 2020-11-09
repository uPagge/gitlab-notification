package org.sadtech.bot.vsc.bitbucketbot.context.repository;

import lombok.NonNull;
import org.sadtech.bot.vsc.bitbucketbot.context.domain.entity.Chat;
import org.sadtech.haiti.context.repository.SimpleManagerRepository;

import java.util.Set;

/**
 * // TODO: 11.10.2020 Добавить описание.
 *
 * @author upagge 11.10.2020
 */
public interface ChatRepository extends SimpleManagerRepository<Chat, String> {

    Set<Long> findAllTelegramIdByKey(@NonNull Set<String> keys);

}
