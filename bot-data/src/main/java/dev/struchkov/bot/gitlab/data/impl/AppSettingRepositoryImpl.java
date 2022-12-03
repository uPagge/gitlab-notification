package dev.struchkov.bot.gitlab.data.impl;

import dev.struchkov.bot.gitlab.context.domain.entity.AppSetting;
import dev.struchkov.bot.gitlab.context.repository.AppSettingRepository;
import dev.struchkov.bot.gitlab.data.jpa.AppSettingJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * @author upagge 16.01.2021
 */
@Repository
@RequiredArgsConstructor
public class AppSettingRepositoryImpl implements AppSettingRepository {

    private final AppSettingJpaRepository jpaRepository;

    @Override
    public AppSetting save(AppSetting appSetting) {
        return jpaRepository.save(appSetting);
    }

    @Override
    public Optional<AppSetting> findById(Long key) {
        return jpaRepository.findById(key);
    }
}
