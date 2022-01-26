package dev.struchkov.bot.gitlab.context.domain.notify;

import dev.struchkov.bot.gitlab.context.service.AppSettingService;
import lombok.Builder;
import lombok.Getter;

/**
 * // TODO: 20.09.2020 Добавить описание.
 *
 * @author upagge 20.09.2020
 */
@Getter
public class SimpleTextNotify extends Notify {

    private final String message;

    @Builder
    private SimpleTextNotify(String message) {
        this.message = message;
    }

    @Override
    public String generateMessage(AppSettingService appSettingService) {
        return message;
    }

}
