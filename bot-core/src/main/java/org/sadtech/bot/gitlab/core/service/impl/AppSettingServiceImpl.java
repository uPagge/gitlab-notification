package org.sadtech.bot.gitlab.core.service.impl;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.sadtech.bot.gitlab.context.domain.AppLocale;
import org.sadtech.bot.gitlab.context.domain.entity.AppSetting;
import org.sadtech.bot.gitlab.context.repository.AppSettingRepository;
import org.sadtech.bot.gitlab.context.service.AppSettingService;
import org.sadtech.haiti.context.exception.NotFoundException;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.util.Arrays;

/**
 * // TODO: 16.01.2021 Добавить описание.
 *
 * @author upagge 16.01.2021
 */
@Service
@RequiredArgsConstructor
public class AppSettingServiceImpl implements AppSettingService {

    private static final Long KEY = 1L;
    private static final NotFoundException EXCEPTION = new NotFoundException("Ошибка, невозможно найти настройки приложения, проверьте базу данных.");
    private final AppSettingRepository appSettingRepository;

    private final MessageSource messageSource;

    @Override
    public boolean isFirstStart() {
        return appSettingRepository.findById(KEY)
                .orElseThrow(() -> EXCEPTION)
                .isFirstStart();
    }

    @Override
    public void disableFirstStart() {
        final AppSetting appSetting = appSettingRepository.findById(KEY).orElseThrow(() -> EXCEPTION);
        appSetting.setFirstStart(false);
        appSettingRepository.save(appSetting);
    }

    @Override
    public String getMessage(@NonNull String label) {
        return messageSource.getMessage(
                label, null, appSettingRepository.findById(KEY)
                        .orElseThrow(() -> EXCEPTION)
                        .getAppLocale().getValue()
        );
    }

    @Override
    public String getMessage(@NonNull String label, String... params) {
        final Object[] paramsArray = Arrays.stream(params).toArray();
        return messageSource.getMessage(
                label,
                paramsArray,
                appSettingRepository.findById(KEY)
                        .orElseThrow(() -> EXCEPTION)
                        .getAppLocale().getValue()
        );
    }

    @Override
    public void setLocale(@NonNull AppLocale appLocale) {
        final AppSetting appSetting = appSettingRepository.findById(KEY).orElseThrow(() -> EXCEPTION);
        appSetting.setAppLocale(appLocale);
        appSettingRepository.save(appSetting);
    }

}
