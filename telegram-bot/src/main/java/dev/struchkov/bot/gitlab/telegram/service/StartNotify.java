package dev.struchkov.bot.gitlab.telegram.service;

import dev.struchkov.bot.gitlab.context.service.AppSettingService;
import dev.struchkov.bot.gitlab.core.config.properties.AppProperty;
import dev.struchkov.bot.gitlab.core.config.properties.PersonProperty;
import dev.struchkov.godfather.main.domain.BoxAnswer;
import dev.struchkov.godfather.telegram.main.context.BoxAnswerPayload;
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
                            "Hello \uD83D\uDC4B\nI wish you a productive day \uD83C\uDF40" +
                            "\n-- -- -- -- --\n" +
                            "\uD83E\uDD16 Bot Version " + appProperty.getVersion() +
                            "\n\uD83D\uDC68\u200D\uD83D\uDCBB️ Developer: [uPagge](https://mark.struchkov.dev)" +
                            "\n\uD83D\uDC1B Issue: [GitHub Issue](https://github.com/uPagge/gitlab-notification/issues)"
                    )
                    .payload(BoxAnswerPayload.DISABLE_WEB_PAGE_PREVIEW, true)
                    .build();

            sending.send(boxAnswer);
        }
    }

}
