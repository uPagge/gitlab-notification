package org.sadtech.bot.bitbucketbot.service;

import lombok.NonNull;
import org.sadtech.bot.bitbucketbot.domain.entity.User;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface UserService {

    Optional<User> getByLogin(String login);

    Set<String> existsByLogin(@NonNull Set<String> logins);

    User reg(@NonNull User user);

    List<User> addAll(Set<User> newUsers);

    List<User> getAllRegister();

    Optional<Long> getTelegramIdByLogin(@NonNull String login);

    Set<Long> getAllTelegramIdByLogin(Set<String> logins);

}
