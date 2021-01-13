package org.sadtech.bot.vsc.bitbucketbot.context.service;

import lombok.NonNull;
import org.sadtech.bot.vsc.bitbucketbot.context.domain.entity.Person;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface PersonService {

    Set<String> existsByLogin(@NonNull Set<String> logins);

    Person reg(@NonNull Person user);

    List<Person> getAllRegister();

    Set<Long> getAllTelegramIdByLogin(Set<String> logins);

    Person create(@NonNull Person person);

    List<Person> createAll(Collection<Person> newUsers);

    boolean existsByTelegram(Long telegramId);

    Optional<Person> getByTelegramId(@NonNull Long telegramId);

}
