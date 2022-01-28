package dev.struchkov.bot.gitlab.core.service.impl;

import dev.struchkov.bot.gitlab.context.domain.AppLocale;
import dev.struchkov.bot.gitlab.context.domain.entity.AppSetting;
import dev.struchkov.bot.gitlab.context.repository.AppSettingRepository;
import dev.struchkov.bot.gitlab.context.service.AppSettingService;
import dev.struchkov.haiti.context.exception.NotFoundException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Locale;
import java.util.function.Supplier;

/**
 * Сервис отвечает за пользовательские настройки приложения.
 *
 * @author upagge 16.01.2021
 */
@Service
@RequiredArgsConstructor
public class AppSettingServiceImpl implements AppSettingService {

    private static final Long KEY = 1L;
    public static final Supplier<NotFoundException> NOT_FOUND_SETTINGS = NotFoundException.supplier("Ошибка, невозможно найти настройки приложения, проверьте базу данных.");
    private final AppSettingRepository appSettingRepository;

    private final MessageSource messageSource;

    @Override
    public boolean isFirstStart() {
        return getAppSetting().isFirstStart();
    }

    @Override
    public void disableFirstStart() {
        final AppSetting appSetting = getAppSetting();
        appSetting.setFirstStart(false);
        appSettingRepository.save(appSetting);
    }

    @Override
    public String getMessage(@NonNull String label) {
        final Locale value = getAppSetting().getAppLocale().getValue();
        return messageSource.getMessage(label, null, value);
    }

    @Override
    public String getMessage(@NonNull String label, Object... params) {
        final Object[] paramsArray = Arrays.stream(params).toArray();
        return messageSource.getMessage(
                label,
                paramsArray,
                getAppSetting().getAppLocale().getValue()
        );
    }

    @Override
    public void setLocale(@NonNull AppLocale appLocale) {
        final AppSetting appSetting = getAppSetting();
        appSetting.setAppLocale(appLocale);
        appSettingRepository.save(appSetting);
    }

    private AppSetting getAppSetting() {
        return appSettingRepository.findById(KEY)
                .orElseThrow(NOT_FOUND_SETTINGS);
    }

}
