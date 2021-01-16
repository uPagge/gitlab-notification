package org.sadtech.bot.gitlab.data.impl;

import org.sadtech.bot.gitlab.context.domain.entity.AppSetting;
import org.sadtech.bot.gitlab.context.repository.AppSettingRepository;
import org.sadtech.haiti.database.repository.manager.AbstractSimpleManagerRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * // TODO: 16.01.2021 Добавить описание.
 *
 * @author upagge 16.01.2021
 */
@Repository
public class AppSettingRepositoryImpl extends AbstractSimpleManagerRepository<AppSetting, Long> implements AppSettingRepository {

    public AppSettingRepositoryImpl(JpaRepository<AppSetting, Long> jpaRepository) {
        super(jpaRepository);
    }

}
