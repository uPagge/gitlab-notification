package dev.struchkov.bot.gitlab.core.service.impl;

import dev.struchkov.bot.gitlab.context.domain.entity.AppSetting;
import dev.struchkov.bot.gitlab.context.domain.notify.level.DiscussionLevel;
import dev.struchkov.bot.gitlab.context.repository.AppSettingRepository;
import dev.struchkov.bot.gitlab.context.service.AppSettingService;
import dev.struchkov.haiti.context.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.function.Supplier;

import static dev.struchkov.haiti.context.exception.NotFoundException.notFoundException;

/**
 * Сервис отвечает за пользовательские настройки приложения.
 *
 * @author upagge 16.01.2021
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AppSettingServiceImpl implements AppSettingService {

    private static final Long KEY = 1L;
    public static final Supplier<NotFoundException> NOT_FOUND_SETTINGS = notFoundException("Ошибка, невозможно найти настройки приложения, проверьте базу данных.");
    private final AppSettingRepository appSettingRepository;

    @Override
    @Transactional(readOnly = true)
    public boolean isFirstStart() {
        return getAppSetting().isFirstStart();
    }

    @Override
    @Transactional
    public void disableFirstStart() {
        final AppSetting appSetting = getAppSetting();
        appSetting.setFirstStart(false);
        log.info("Первичная настройка закончена");
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isEnableAllNotify() {
        return getAppSetting().isEnableNotify();
    }

    @Override
    @Transactional
    public void turnOnAllNotify() {
        final AppSetting appSetting = getAppSetting();
        appSetting.setEnableNotify(true);
        log.info("Получение всех уведомлений активировано");
    }

    @Override
    @Transactional
    public void privateProjectScan(boolean enable) {
        final AppSetting appSetting = getAppSetting();
        appSetting.setProjectPrivateScan(enable);
    }

    @Override
    @Transactional
    public void ownerProjectScan(boolean enable) {
        final AppSetting appSetting = getAppSetting();
        appSetting.setProjectOwnerScan(enable);
    }

    @Override
    public boolean isOwnerProjectScan() {
        return getAppSetting().isProjectOwnerScan();
    }

    @Override
    public boolean isPrivateProjectScan() {
        return getAppSetting().isProjectPrivateScan();
    }

    @Override
    public DiscussionLevel getLevelDiscussionNotify() {
        return getAppSetting().getDiscussionNotifyLevel();
    }

    @Override
    @Transactional
    public void setDiscussionLevel(DiscussionLevel level) {
        final AppSetting appSetting = getAppSetting();
        appSetting.setDiscussionNotifyLevel(level);
    }

    private AppSetting getAppSetting() {
        return appSettingRepository.findById(KEY)
                .orElseThrow(NOT_FOUND_SETTINGS);
    }

}
