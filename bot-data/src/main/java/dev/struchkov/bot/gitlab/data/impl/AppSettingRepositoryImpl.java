package dev.struchkov.bot.gitlab.data.impl;

import dev.struchkov.bot.gitlab.context.domain.entity.AppSetting;
import dev.struchkov.bot.gitlab.context.repository.AppSettingRepository;
import dev.struchkov.haiti.database.repository.manager.AbstractSimpleManagerRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author upagge 16.01.2021
 */
@Repository
public class AppSettingRepositoryImpl extends AbstractSimpleManagerRepository<AppSetting, Long> implements AppSettingRepository {

    public AppSettingRepositoryImpl(JpaRepository<AppSetting, Long> jpaRepository) {
        super(jpaRepository);
    }

}
