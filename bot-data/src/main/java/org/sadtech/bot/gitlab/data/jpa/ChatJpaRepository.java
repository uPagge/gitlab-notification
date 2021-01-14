package org.sadtech.bot.gitlab.data.jpa;

import org.sadtech.bot.gitlab.context.domain.entity.Chat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.query.Param;

import java.util.Set;

/**
 * // TODO: 11.10.2020 Добавить описание.
 *
 * @author upagge 11.10.2020
 */
@NoRepositoryBean
public interface ChatJpaRepository extends JpaRepository<Chat, String> {

//    @Query("SELECT c.telegramId FROM Chat c WHERE c.key IN :keys AND c.telegramId IS NOT NULL")
    Set<Long> findAllTelegramIdByKey(@Param("keys") Set<String> keys);

}
