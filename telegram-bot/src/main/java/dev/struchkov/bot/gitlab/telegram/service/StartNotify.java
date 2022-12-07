package dev.struchkov.bot.gitlab.telegram.service;

import dev.struchkov.bot.gitlab.context.domain.notify.SimpleTextNotify;
import dev.struchkov.bot.gitlab.context.service.AppSettingService;
import dev.struchkov.bot.gitlab.context.service.NotifyService;
import dev.struchkov.bot.gitlab.core.config.properties.AppProperty;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
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
                            .message("Hello. I wish you a productive day :)" +
                                    "\n-- -- -- -- --\n" +
                                    "Version " + appProperty.getVersion() + " | Developer: [uPagge](https://mark.struchkov.dev)")
                            .build()
            );
        }
    }

}
