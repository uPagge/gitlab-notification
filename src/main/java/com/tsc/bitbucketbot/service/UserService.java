package com.tsc.bitbucketbot.service;

import com.tsc.bitbucketbot.domain.entity.User;
import lombok.NonNull;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface UserService {

    Optional<User> getByLogin(String login);

    Set<String> existsByLogin(@NonNull Set<String> logins);

    Optional<User> reg(@NonNull User user);

    List<User> addAll(Set<User> newUsers);

    List<User> getAllRegister();

    Optional<Long> getTelegramIdByLogin(@NonNull String login);

    List<Long> getAllTelegramIdByLogin(Set<String> logins);

}
