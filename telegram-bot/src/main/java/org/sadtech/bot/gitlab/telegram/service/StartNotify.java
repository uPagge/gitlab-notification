package org.sadtech.bot.gitlab.telegram.service;

import lombok.RequiredArgsConstructor;
import org.sadtech.bot.gitlab.context.domain.notify.SimpleTextNotify;
import org.sadtech.bot.gitlab.context.service.AppSettingService;
import org.sadtech.bot.gitlab.context.service.NotifyService;
import org.sadtech.bot.gitlab.core.config.properties.AppProperty;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * // TODO: 19.01.2021 Добавить описание.
 *
 * @author upagge 19.01.2021
 */
@Component
@RequiredArgsConstructor
public class StartNotify {

    private final NotifyService notifyService;
    private final AppProperty appProperty;
    private final AppSettingService settingService;

    @PostConstruct
    public void sendStartNotification() {
        if (!settingService.isFirstStart()) {
            notifyService.send(
                    SimpleTextNotify.builder()
                            .message("Привет. Желаю продуктивного дня :)" +
                                    "\n-- -- -- -- --\n" +
                                    "Version " + appProperty.getVersion() + " | Developer: [uPagge](https://uPagge.ru)")
                            .build()
            );
        }
    }

}
