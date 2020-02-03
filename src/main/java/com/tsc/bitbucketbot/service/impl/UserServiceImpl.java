package com.tsc.bitbucketbot.service.impl;

import com.tsc.bitbucketbot.domain.entity.User;
import com.tsc.bitbucketbot.repository.UserRepository;
import com.tsc.bitbucketbot.service.UserService;
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

    @Override
    public boolean existsByTelegramId(@NonNull Long chatId) {
        return userRepository.existsByTelegramId(chatId);
    }

    @Override
    public User add(@NonNull User user) {
        return userRepository.save(user);
    }

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
    public List<User> addAll(@NonNull Set<User> users) {
        return userRepository.saveAll(users);
    }

    @Override
    public List<User> getAllRegistered() {
        return userRepository.findAllRegistered();
    }

}
