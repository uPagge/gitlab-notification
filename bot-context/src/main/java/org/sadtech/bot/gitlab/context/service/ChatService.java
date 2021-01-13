package org.sadtech.bot.gitlab.context.service;

import lombok.NonNull;

import java.util.Set;

/**
 * // TODO: 11.10.2020 Добавить описание.
 *
 * @author upagge 11.10.2020
 */
public interface ChatService {

    Set<Long> getAllTelegramIdByKey(@NonNull Set<String> keys);

}
