package org.sadtech.bot.vcs.core.service;

import lombok.NonNull;
import org.sadtech.bot.vcs.core.domain.entity.Person;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface PersonService {

    Optional<Person> getByLogin(String login);

    Set<String> existsByLogin(@NonNull Set<String> logins);

    boolean existsByLogin(@NonNull String login);

    Person reg(@NonNull Person user);

    List<Person> getAllRegister();

    Optional<Long> getTelegramIdByLogin(@NonNull String login);

    Set<Long> getAllTelegramIdByLogin(Set<String> logins);

    Person create(@NonNull Person person);

    List<Person> createAll(Collection<Person> newUsers);

}
