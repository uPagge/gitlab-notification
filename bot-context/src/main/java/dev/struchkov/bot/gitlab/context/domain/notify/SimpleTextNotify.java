package dev.struchkov.bot.gitlab.context.domain.notify;

import dev.struchkov.bot.gitlab.context.service.AppSettingService;
import lombok.Builder;

/**
 * @author upagge 20.09.2020
 */
public record SimpleTextNotify(String message) implements Notify {

    @Builder
    public SimpleTextNotify {
    }

    @Override
    public String generateMessage(AppSettingService appSettingService) {
        return message;
    }

}
