package org.sadtech.bot.vcs.core.repository.jpa;

import org.sadtech.bot.vcs.core.domain.entity.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

/**
 * TODO: Добавить описание класса.
 *
 * @author upagge [30.01.2020]
 */
@Repository
public interface PersonJpaRepository extends JpaRepository<Person, String> {

    boolean existsByTelegramId(Long chatId);

    boolean existsByLogin(String login);

    List<Person> findAllByTelegramIdNotNullAndTokenNotNull();

    @Query("SELECT u.telegramId FROM Person u WHERE u.login=:login")
    Long findTelegramIdByLogin(String login);

    @Query("SELECT u.telegramId FROM Person u WHERE u.login IN :logins AND u.telegramId IS NOT NULL")
    Set<Long> findAllTelegramIdByLogin(Set<String> logins);

}
