package org.sadtech.bot.vcs.bitbucketbot.data.impl;

import lombok.NonNull;
import org.sadtech.bot.vcs.bitbucketbot.data.jpa.NotifySettingJpaRepository;
import org.sadtech.bot.vsc.bitbucketbot.context.domain.entity.NotifySetting;
import org.sadtech.bot.vsc.bitbucketbot.context.repository.NotifySettingRepository;
import org.sadtech.haiti.database.repository.manager.AbstractSimpleManagerRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * // TODO: 20.09.2020 Добавить описание.
 *
 * @author upagge 20.09.2020
 */
@Repository
public class NotifySettingRepositoryImpl extends AbstractSimpleManagerRepository<NotifySetting, String> implements NotifySettingRepository {

    private final NotifySettingJpaRepository jpaRepository;

    public NotifySettingRepositoryImpl(NotifySettingJpaRepository jpaRepository) {
        super(jpaRepository);
        this.jpaRepository = jpaRepository;
    }

    @Override
    public boolean isNotification(@NonNull String login) {
        return jpaRepository.findByLoginAndStartReceivingAfter(login, LocalDateTime.now());
    }

    @Override
    public Set<String> isNotification(@NonNull Set<String> logins) {
        return jpaRepository.findAllByLoginInAndStartReceivingAfter(logins, LocalDateTime.now());
    }

}
