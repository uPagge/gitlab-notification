package dev.struchkov.bot.gitlab.data.impl;

import dev.struchkov.bot.gitlab.context.domain.entity.NotifySetting;
import dev.struchkov.bot.gitlab.context.repository.NotifySettingRepository;
import dev.struchkov.bot.gitlab.data.jpa.NotifySettingJpaRepository;
import dev.struchkov.haiti.database.repository.manager.AbstractSimpleManagerRepository;

/**
 * // TODO: 20.09.2020 Добавить описание.
 *
 * @author upagge 20.09.2020
 */
//@Repository
public class NotifySettingRepositoryImpl extends AbstractSimpleManagerRepository<NotifySetting, String> implements NotifySettingRepository {

    private final NotifySettingJpaRepository jpaRepository;

    public NotifySettingRepositoryImpl(NotifySettingJpaRepository jpaRepository) {
        super(jpaRepository);
        this.jpaRepository = jpaRepository;
    }

}
