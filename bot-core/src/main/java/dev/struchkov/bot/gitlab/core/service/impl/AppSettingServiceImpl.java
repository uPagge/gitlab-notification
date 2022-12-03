package dev.struchkov.bot.gitlab.core.service.impl;

import dev.struchkov.bot.gitlab.context.domain.entity.AppSetting;
import dev.struchkov.bot.gitlab.context.repository.AppSettingRepository;
import dev.struchkov.bot.gitlab.context.service.AppSettingService;
import dev.struchkov.haiti.context.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.util.function.Supplier;

import static dev.struchkov.haiti.context.exception.NotFoundException.notFoundException;

/**
 * Сервис отвечает за пользовательские настройки приложения.
 *
 * @author upagge 16.01.2021
 */
@Service
@RequiredArgsConstructor
public class AppSettingServiceImpl implements AppSettingService {

    private static final Long KEY = 1L;
    public static final Supplier<NotFoundException> NOT_FOUND_SETTINGS = notFoundException("Ошибка, невозможно найти настройки приложения, проверьте базу данных.");
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

    private AppSetting getAppSetting() {
        return appSettingRepository.findById(KEY)
                .orElseThrow(NOT_FOUND_SETTINGS);
    }

}
