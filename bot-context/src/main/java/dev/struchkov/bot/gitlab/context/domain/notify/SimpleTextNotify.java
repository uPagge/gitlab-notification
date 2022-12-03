package dev.struchkov.bot.gitlab.context.domain.notify;

import lombok.Builder;

/**
 * @author upagge 20.09.2020
 */
public record SimpleTextNotify(String message) implements Notify {

    @Builder
    public SimpleTextNotify {
    }

    @Override
    public String generateMessage() {
        return message;
    }

}
