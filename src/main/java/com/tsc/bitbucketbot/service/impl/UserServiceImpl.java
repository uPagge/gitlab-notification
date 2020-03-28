package com.tsc.bitbucketbot.service.impl;

import com.tsc.bitbucketbot.config.BitbucketConfig;
import com.tsc.bitbucketbot.domain.entity.User;
import com.tsc.bitbucketbot.dto.bitbucket.sheet.PullRequestSheetJson;
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

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final BitbucketConfig bitbucketConfig;

    @Override
    public Optional<User> getByLogin(String login) {
        return userRepository.findById(login);
    }

    @Override
    public Set<String> existsByLogin(@NonNull Set<String> logins) {
        return logins.stream().filter(userRepository::existsById).collect(Collectors.toSet());
    }

    @Override
    public Optional<User> reg(@NonNull User user) {
        final Optional<User> optUser = userRepository.findByLogin(user.getLogin());
        if (optUser.isPresent()) {
            final User oldUser = optUser.get();
            if (oldUser.getTelegramId() == null) {
                Optional<PullRequestSheetJson> sheetJson = Utils.urlToJson(bitbucketConfig.getUrlPullRequestClose(), user.getToken(), PullRequestSheetJson.class);
                if (sheetJson.isPresent()) {
                    oldUser.setTelegramId(user.getTelegramId());
                    return Optional.of(userRepository.save(oldUser));
                } else {
                    throw new RegException("Ваш токен не валиден");
                }
            }
        }
        throw new RegException("Пользователь не найден, подождите обновление базы пользователей!");
    }

    @Override
    public List<User> addAll(Set<User> newUsers) {
        return userRepository.saveAll(newUsers);
    }

    @Override
    public List<User> getAllRegister() {
        return userRepository.findAllByTelegramIdNotNullAndTokenNotNull();
    }

    @Override
    public Optional<Long> getTelegramIdByLogin(@NonNull String login) {
        return Optional.ofNullable(userRepository.findTelegramIdByLogin(login));
    }

    @Override
    public List<Long> getAllTelegramIdByLogin(Set<String> logins) {
        return userRepository.findAllTelegramIdByLogin(logins);
    }

}
