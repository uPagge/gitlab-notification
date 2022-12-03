package dev.struchkov.bot.gitlab.context.repository;

import dev.struchkov.bot.gitlab.context.domain.entity.AppSetting;

import java.util.Optional;

/**
 * @author upagge 16.01.2021
 */
public interface AppSettingRepository {

    AppSetting save(AppSetting appSetting);

    Optional<AppSetting> findById(Long key);
}
