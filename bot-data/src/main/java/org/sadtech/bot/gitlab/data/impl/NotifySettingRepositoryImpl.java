package org.sadtech.bot.gitlab.data.impl;

import org.sadtech.bot.gitlab.context.domain.entity.NotifySetting;
import org.sadtech.bot.gitlab.context.repository.NotifySettingRepository;
import org.sadtech.bot.gitlab.data.jpa.NotifySettingJpaRepository;
import org.sadtech.haiti.database.repository.manager.AbstractSimpleManagerRepository;

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
