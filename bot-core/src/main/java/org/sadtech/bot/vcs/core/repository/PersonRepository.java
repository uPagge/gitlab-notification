package org.sadtech.bot.vcs.core.repository;

import lombok.NonNull;
import org.sadtech.bot.vcs.core.domain.entity.Person;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * // TODO: 06.09.2020 Добавить описание.
 *
 * @author upagge 06.09.2020
 */
public interface PersonRepository {

    Person save(@NonNull Person person);

    boolean existsByTelegramId(Long chatId);

    boolean existsByLogin(String login);

    List<Person> findAllByTelegramIdNotNullAndTokenNotNull();

    Long findTelegramIdByLogin(String login);

    Set<Long> findAllTelegramIdByLogin(Set<String> logins);

    Optional<Person> findByLogin(@NonNull String login);

    Optional<Person> findByTelegramId(@NonNull Long telegramId);

}
