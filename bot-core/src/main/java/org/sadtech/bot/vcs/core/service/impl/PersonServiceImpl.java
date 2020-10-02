package org.sadtech.bot.vcs.core.service.impl;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.sadtech.basic.core.util.Assert;
import org.sadtech.bot.vcs.core.config.properties.BitbucketProperty;
import org.sadtech.bot.vcs.core.domain.entity.NotifySetting;
import org.sadtech.bot.vcs.core.domain.entity.Person;
import org.sadtech.bot.vcs.core.exception.RegException;
import org.sadtech.bot.vcs.core.repository.PersonRepository;
import org.sadtech.bot.vcs.core.service.NotifyService;
import org.sadtech.bot.vcs.core.service.PersonService;
import org.sadtech.bot.vcs.core.service.Utils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class PersonServiceImpl implements PersonService {

    private final PersonRepository personRepository;
    private final BitbucketProperty bitbucketProperty;

    private final NotifyService notifyService;

    public PersonServiceImpl(
            PersonRepository personRepository,
            BitbucketProperty bitbucketProperty,
            @Lazy NotifyService notifyService
    ) {
        this.personRepository = personRepository;
        this.bitbucketProperty = bitbucketProperty;
        this.notifyService = notifyService;
    }

    @Override
    public Set<String> existsByLogin(@NonNull Set<String> logins) {
        return logins.stream()
                .filter(personRepository::existsByLogin)
                .collect(Collectors.toSet());
    }

    @Override
    public Person reg(@NonNull Person user) {
        final Optional<Person> optUser = personRepository.findByLogin(user.getLogin());
        if (optUser.isPresent()) {
            final Person oldUser = optUser.get();
            if (oldUser.getTelegramId() == null) {
                Optional<Object> sheetJson = Utils.urlToJson(bitbucketProperty.getUrlPullRequestClose(), user.getToken(), Object.class);
                if (sheetJson.isPresent()) {
                    oldUser.setTelegramId(user.getTelegramId());

                    defaultSettings(oldUser);

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

    private void defaultSettings(Person person) {
        final NotifySetting notifySetting = new NotifySetting();
        notifySetting.setLogin(person.getLogin());
        notifySetting.setStartReceiving(LocalDateTime.now());
        notifyService.saveSettings(notifySetting);
    }

    @Override
    public List<Person> getAllRegister() {
        return personRepository.findAllByTelegramIdNotNullAndTokenNotNull();
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

    @Override
    public boolean existsByTelegram(Long telegramId) {
        return personRepository.existsByTelegramId(telegramId);
    }

    @Override
    public Optional<Person> getByTelegramId(@NonNull Long telegramId) {
        return personRepository.findByTelegramId(telegramId);
    }

}
