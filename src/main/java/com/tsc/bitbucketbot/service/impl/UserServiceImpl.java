package com.tsc.bitbucketbot.service.impl;

import com.tsc.bitbucketbot.bitbucket.sheet.PullRequestSheetJson;
import com.tsc.bitbucketbot.config.BitbucketConfig;
import com.tsc.bitbucketbot.domain.entity.User;
import com.tsc.bitbucketbot.exception.RegException;
import com.tsc.bitbucketbot.repository.jpa.UserRepository;
import com.tsc.bitbucketbot.service.UserService;
import com.tsc.bitbucketbot.service.Utils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * TODO: Добавить описание класса.
 *
 * @author upagge [30.01.2020]
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final BitbucketConfig bitbucketConfig;

    @Override
    public List<User> getAll() {
        return userRepository.findAll();
    }

    @Override
    public Optional<User> getByLogin(String login) {
        return userRepository.findById(login);
    }

    @Override
    public Optional<User> update(User user) {
        if (userRepository.existsById(user.getLogin())) {
            return Optional.of(userRepository.save(user));
        }
        return Optional.empty();
    }

    @Override
    public Set<String> existsByLogin(@NonNull Set<String> logins) {
        return logins.stream().filter(userRepository::existsById).collect(Collectors.toSet());
    }

    @Override
    public Optional<User> reg(@NonNull User user) {
        if (userRepository.existsByLogin(user.getLogin()) && !userRepository.existsByTelegramId(user.getTelegramId())) {
            Optional<PullRequestSheetJson> sheetJson = Utils.urlToJson(bitbucketConfig.getUrlPullRequestClose(), user.getToken(), PullRequestSheetJson.class);
            if (sheetJson.isPresent()) {
                return Optional.of(userRepository.save(user));
            } else {
                throw new RegException("Ваш токен не валиден");
            }
        } else {
            throw new RegException("Пользователь с таким логином или телеграмом уже есть в системе");
        }
    }

    @Override
    public List<User> addAll(Set<User> newUsers) {
        return userRepository.saveAll(newUsers);
    }

}
