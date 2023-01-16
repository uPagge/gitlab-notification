package dev.struchkov.bot.gitlab.core.utils;

import static dev.struchkov.haiti.utils.Inspector.isNotNull;

/**
 * Утилитарная сущность для {@link HttpParse}. Упрощает сохранения в константы заголовков для запроса.
 *
 * @author upagge 23.12.2020
 */
public class HttpHeader {

    private final String name;
    private final String value;

    private HttpHeader(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public static HttpHeader of(String name, String value) {
        isNotNull(name, value);
        return new HttpHeader(name, value);
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

}
