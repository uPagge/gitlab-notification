package dev.struchkov.bot.gitlab.context.service;

import dev.struchkov.bot.gitlab.context.domain.AppLocale;
import lombok.NonNull;

/**
 * // TODO: 16.01.2021 Добавить описание.
 *
 * @author upagge 16.01.2021
 */
public interface AppSettingService {

    boolean isFirstStart();

    void disableFirstStart();

    String getMessage(@NonNull String label);

    String getMessage(@NonNull String label, Object... params);

    void setLocale(@NonNull AppLocale appLocale);

}
