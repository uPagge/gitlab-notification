package org.sadtech.bot.bitbucketbot.service.impl;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sadtech.basic.core.util.Assert;
import org.sadtech.bot.bitbucketbot.config.properties.BitbucketProperty;
import org.sadtech.bot.bitbucketbot.domain.entity.Person;
import org.sadtech.bot.bitbucketbot.dto.bitbucket.sheet.PullRequestSheetJson;
import org.sadtech.bot.bitbucketbot.exception.RegException;
import org.sadtech.bot.bitbucketbot.repository.PersonRepository;
import org.sadtech.bot.bitbucketbot.service.PersonService;
import org.sadtech.bot.bitbucketbot.service.Utils;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PersonServiceImpl implements PersonService {

    private final PersonRepository personRepository;
    private final BitbucketProperty bitbucketProperty;

    @Override
    public Optional<Person> getByLogin(String login) {
        return personRepository.findByLogin(login);
    }

    @Override
    public Set<String> existsByLogin(@NonNull Set<String> logins) {
        return logins.stream()
                .filter(personRepository::existsByLogin)
                .collect(Collectors.toSet());
    }

    @Override
    public boolean existsByLogin(@NonNull String login) {
        return personRepository.existsByLogin(login);
    }

    @Override
    public Person reg(@NonNull Person user) {
        final Optional<Person> optUser = personRepository.findByLogin(user.getLogin());
        if (optUser.isPresent()) {
            final Person oldUser = optUser.get();
            if (oldUser.getTelegramId() == null) {
                Optional<PullRequestSheetJson> sheetJson = Utils.urlToJson(bitbucketProperty.getUrlPullRequestClose(), user.getToken(), PullRequestSheetJson.class);
                if (sheetJson.isPresent()) {
                    oldUser.setTelegramId(user.getTelegramId());
                    return personRepository.save(oldUser);
                } else {
                    throw new RegException("Ваш токен не валиден");
                }
            } else {
                throw new RegException("Вы уже авторизованы в системе");
            }
        }
        throw new RegException("Пользователь не найден, подождите обновление базы пользователей!");
    }

    @Override
    public List<Person> getAllRegister() {
        return personRepository.findAllByTelegramIdNotNullAndTokenNotNull();
    }

    @Override
    public Optional<Long> getTelegramIdByLogin(@NonNull String login) {
        return Optional.ofNullable(personRepository.findTelegramIdByLogin(login));
    }

    @Override
    public Set<Long> getAllTelegramIdByLogin(Set<String> logins) {
        return personRepository.findAllTelegramIdByLogin(logins);
    }

    @Override
    public Person create(@NonNull Person person) {
        Assert.isNotNull(person.getLogin(), "При создании пользователя должен присутствовать логин");
        return personRepository.save(person);
    }

    @Override
    public List<Person> createAll(Collection<Person> newPersons) {
        return newPersons.stream().map(this::create).collect(Collectors.toList());
    }

}
