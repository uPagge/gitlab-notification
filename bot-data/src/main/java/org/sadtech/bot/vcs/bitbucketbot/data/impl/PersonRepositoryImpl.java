package org.sadtech.bot.vcs.bitbucketbot.data.impl;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sadtech.bot.vcs.bitbucketbot.data.jpa.PersonJpaRepository;
import org.sadtech.bot.vsc.bitbucketbot.context.domain.entity.Person;
import org.sadtech.bot.vsc.bitbucketbot.context.repository.PersonRepository;
import org.springframework.dao.InvalidDataAccessResourceUsageException;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * // TODO: 06.09.2020 Добавить описание.
 *
 * @author upagge 06.09.2020
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class PersonRepositoryImpl implements PersonRepository {

    private final PersonJpaRepository jpaRepository;

    @Override
    public Person save(@NonNull Person person) {
        return jpaRepository.save(person);
    }

    @Override
    public boolean existsByTelegramId(Long chatId) {
        return jpaRepository.existsByTelegramId(chatId);
    }

    @Override
    public boolean existsByLogin(String login) {
        try {
            return jpaRepository.existsByLogin(login);
        } catch (InvalidDataAccessResourceUsageException e) {
            log.error(e.getMessage());
        }
        return false;
    }

    @Override
    public List<Person> findAllByTelegramIdNotNullAndTokenNotNull() {
        try {
            return jpaRepository.findAllByTelegramIdNotNullAndTokenNotNull();
        } catch (InvalidDataAccessResourceUsageException e) {
            log.error(e.getMessage());
        }
        return Collections.emptyList();
    }

    @Override
    public Set<Long> findAllTelegramIdByLogin(Set<String> logins) {
        return jpaRepository.findAllTelegramIdByLogin(logins);
    }

    @Override
    public Optional<Person> findByLogin(@NonNull String login) {
        return jpaRepository.findById(login);
    }

    @Override
    public Optional<Person> findByTelegramId(@NonNull Long telegramId) {
        return jpaRepository.findByTelegramId(telegramId);
    }

}
