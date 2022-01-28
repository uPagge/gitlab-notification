package dev.struchkov.bot.gitlab.context.repository;

import dev.struchkov.bot.gitlab.context.domain.entity.AppSetting;
import dev.struchkov.haiti.context.repository.SimpleManagerRepository;

/**
 * @author upagge 16.01.2021
 */
public interface AppSettingRepository extends SimpleManagerRepository<AppSetting, Long> {

}
