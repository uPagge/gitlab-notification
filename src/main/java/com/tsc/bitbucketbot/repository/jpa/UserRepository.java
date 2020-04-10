package com.tsc.bitbucketbot.repository.jpa;

import com.tsc.bitbucketbot.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * TODO: Добавить описание класса.
 *
 * @author upagge [30.01.2020]
 */
@Repository
public interface UserRepository extends JpaRepository<User, String> {

    boolean existsByTelegramId(Long chatId);

    boolean existsByLogin(String login);

    List<User> findAllByTelegramIdNotNullAndTokenNotNull();

    @Query("SELECT u.telegramId FROM User u WHERE u.login=:login")
    Long findTelegramIdByLogin(String login);

    @Query("SELECT u.telegramId FROM User u WHERE u.login IN :logins AND u.telegramId IS NOT NULL")
    Set<Long> findAllTelegramIdByLogin(Set<String> logins);

    Optional<User> findByLogin(String login);

}
