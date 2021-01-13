package org.sadtech.bot.vcs.bitbucketbot.data.jpa;

import org.sadtech.bot.vsc.bitbucketbot.context.domain.entity.NotifySetting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * // TODO: 20.09.2020 Добавить описание.
 *
 * @author upagge 20.09.2020
 */
public interface NotifySettingJpaRepository extends JpaRepository<NotifySetting, String> {

    boolean findByLoginAndStartReceivingAfter(String login, LocalDateTime date);

    @Query("SELECT n.login FROM NotifySetting n WHERE n.login IN :logins AND n.startReceiving < :date")
    Set<String> findAllByLoginInAndStartReceivingAfter(@Param("logins") Set<String> logins, @Param("date") LocalDateTime date);

}
