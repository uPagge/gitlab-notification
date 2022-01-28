package dev.struchkov.bot.gitlab.context.domain;

import dev.struchkov.haiti.context.exception.NotFoundException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

import java.util.Arrays;
import java.util.Locale;

/**
 * // TODO: 16.01.2021 Добавить описание.
 *
 * @author upagge 16.01.2021
 */
@Getter
@AllArgsConstructor
public enum AppLocale {

    RU("Русский"), EN("English");

    private final String label;

    public static AppLocale of(@NonNull String label) {
        return Arrays.stream(values())
                .filter(appLocale -> appLocale.getLabel().equals(label))
                .findFirst()
                .orElseThrow(NotFoundException.supplier("Ошибка, локализация не найдена. Попробуйте снова."));
    }

    public Locale getValue() {
        return Locale.forLanguageTag(name().toLowerCase());
    }

}
