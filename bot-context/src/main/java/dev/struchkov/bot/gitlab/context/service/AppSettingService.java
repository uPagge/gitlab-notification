package dev.struchkov.bot.gitlab.context.service;

import dev.struchkov.bot.gitlab.context.domain.AppLocale;
import lombok.NonNull;

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

    /**
     * Позволяет получить по ключу текст на языке, который установил пользователь
     *
     * @param label ключ сообщений
     * @return Сообщение на языке пользователя
     */
    String getMessage(@NonNull String label);

    String getMessage(@NonNull String label, Object... params);

    /**
     * Устанавливает язык приложения
     *
     * @param appLocale Язык, который необходимо установить
     */
    void setLocale(@NonNull AppLocale appLocale);

}
