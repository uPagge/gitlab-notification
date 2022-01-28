package dev.struchkov.bot.gitlab.data.jpa;

import dev.struchkov.bot.gitlab.context.domain.entity.AppSetting;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author upagge 16.01.2021
 */
public interface AppSettingJpaRepository extends JpaRepository<AppSetting, Long> {
}
