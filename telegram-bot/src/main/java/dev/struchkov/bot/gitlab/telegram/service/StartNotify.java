package dev.struchkov.bot.gitlab.telegram.service;

import dev.struchkov.bot.gitlab.context.service.AppSettingService;
import dev.struchkov.bot.gitlab.core.config.properties.AppProperty;
import dev.struchkov.bot.gitlab.core.config.properties.PersonProperty;
import dev.struchkov.godfather.main.domain.BoxAnswer;
import dev.struchkov.godfather.telegram.simple.context.service.TelegramSending;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * @author upagge 19.01.2021
 */
@Component
@RequiredArgsConstructor
public class StartNotify {

    private final TelegramSending sending;
    private final AppProperty appProperty;
    private final AppSettingService settingService;
    private final PersonProperty personProperty;

    @PostConstruct
    public void sendStartNotification() {
        if (!settingService.isFirstStart()) {
            final BoxAnswer boxAnswer = BoxAnswer.builder()
                    .recipientPersonId(personProperty.getTelegramId())
                    .message(
                            "Hello. I wish you a productive work day \uD83C\uDF40" +
                            "\n-- -- -- -- --\n" +
                            "Version " + appProperty.getVersion() +
                            "\nDeveloper: [uPagge](https://mark.struchkov.dev)"
                    ).build();

            sending.send(boxAnswer);
        }
    }

}
