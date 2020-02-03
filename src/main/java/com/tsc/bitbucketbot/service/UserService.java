package com.tsc.bitbucketbot.service;

import com.tsc.bitbucketbot.domain.entity.User;
import lombok.NonNull;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * TODO: Добавить описание интерфейса.
 *
 * @author upagge [30.01.2020]
 */
public interface UserService {

    boolean existsByTelegramId(@NonNull final Long chatId);

    User add(@NonNull final User user);

    List<User> getAll();

    Optional<User> getByLogin(String login);

    Optional<User> update(User user);

    Set<String> existsByLogin(@NonNull Set<String> logins);

    List<User> addAll(@NonNull Set<User> users);

    List<User> getAllRegistered();

}
