package org.sadtech.bot.vcs.core.domain.notify;

import lombok.Builder;
import lombok.Getter;

import java.util.Set;

/**
 * // TODO: 20.09.2020 Добавить описание.
 *
 * @author upagge 20.09.2020
 */
@Getter
public class SimpleTextNotify extends Notify {

    private final String message;

    @Builder
    private SimpleTextNotify(Set<String> logins, String message) {
        super(logins);
        this.message = message;
    }

    @Override
    public String generateMessage() {
        return message;
    }
}
