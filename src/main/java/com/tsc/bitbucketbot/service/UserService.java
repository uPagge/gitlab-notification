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

    List<User> getAll();

    Optional<User> getByLogin(String login);

    Set<String> existsByLogin(@NonNull Set<String> logins);

    Optional<User> reg(@NonNull User user);

    List<User> addAll(Set<User> newUsers);

    List<User> getAllRegister();
}
