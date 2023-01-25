package dev.struchkov.bot.gitlab.context.service;

/**
 * Сервис отвечает за пользовательские настройки приложения.
 *
 * @author upagge 16.01.2021
 */
public interface AppSettingService {

    /**
     * Метод позволяет проверить запускается ли приложение впервые.
     *
     * @return true - если это первый запуск
     */
    boolean isFirstStart();

    /**
     * Метод отмечает, что приложение было запущено.
     *
     * @see AppSettingService#isFirstStart()
     */
    void disableFirstStart();

    boolean isEnableAllNotify();

    void turnOnAllNotify();

    void privateProjectScan(boolean enable);

    void ownerProjectScan(boolean enable);

    boolean isOwnerProjectScan();

    boolean isPrivateProjectScan();

}
