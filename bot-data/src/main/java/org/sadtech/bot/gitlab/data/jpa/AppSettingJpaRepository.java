package org.sadtech.bot.gitlab.data.jpa;

import org.sadtech.bot.gitlab.context.domain.entity.AppSetting;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * // TODO: 16.01.2021 Добавить описание.
 *
 * @author upagge 16.01.2021
 */
public interface AppSettingJpaRepository extends JpaRepository<AppSetting, Long> {
}
