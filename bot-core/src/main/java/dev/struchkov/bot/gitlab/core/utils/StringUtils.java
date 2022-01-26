package dev.struchkov.bot.gitlab.core.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * // TODO: 29.09.2020 Добавить описание.
 *
 * @author upagge 29.09.2020
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class StringUtils {

    public static final String H_PRIVATE_TOKEN = "PRIVATE-TOKEN";

    public static String cutOff(String string, int length) {
        if (string != null) {
            return string.length() > length ? string.substring(0, length) + "..." : string;
        }
        return null;
    }

}
