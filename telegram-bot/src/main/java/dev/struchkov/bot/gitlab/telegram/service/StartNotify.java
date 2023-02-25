package dev.struchkov.bot.gitlab.telegram.service;

import dev.struchkov.bot.gitlab.context.service.AppSettingService;
import dev.struchkov.bot.gitlab.context.utils.Icons;
import dev.struchkov.bot.gitlab.core.config.properties.AppProperty;
import dev.struchkov.bot.gitlab.core.config.properties.PersonProperty;
import dev.struchkov.godfather.simple.domain.BoxAnswer;
import dev.struchkov.godfather.simple.domain.SentBox;
import dev.struchkov.godfather.telegram.simple.context.service.TelegramSending;
import dev.struchkov.godfather.telegram.simple.context.service.TelegramService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;

import static dev.struchkov.bot.gitlab.telegram.utils.UnitName.DELETE_MESSAGE;
import static dev.struchkov.godfather.main.domain.keyboard.button.SimpleButton.simpleButton;
import static dev.struchkov.godfather.telegram.domain.keyboard.InlineKeyBoard.inlineKeyBoard;
import static dev.struchkov.godfather.telegram.main.context.BoxAnswerPayload.DISABLE_WEB_PAGE_PREVIEW;
import static dev.struchkov.haiti.utils.Checker.checkNotBlank;

/**
 * @author upagge 19.01.2021
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class StartNotify {

    private final OkHttpClient client = new OkHttpClient();

    private final TelegramSending sending;
    private final TelegramService telegramService;

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
                            "\uD83E\uDD16 Version: " + appProperty.getVersion() +
                            "\n\uD83D\uDC68\u200D\uD83D\uDCBB️ Developer: [Struchkov.Dev](https://mark.struchkov.dev)\n-- -- -- -- --\n" +
                            "\uD83C\uDFE0 [HomePage](https://docs.struchkov.dev/gitlab-notification/) • \uD83D\uDC1B [Report Bug](https://github.com/uPagge/gitlab-notification/issues) • \uD83C\uDD98 [Donation](https://docs.struchkov.dev/gitlab-notification/support-development/)"
                    )
                    .keyBoard(
                            inlineKeyBoard(
                                    simpleButton(Icons.VIEW, DELETE_MESSAGE),
                                    simpleButton("Open Menu", "/start")
                            )
                    )
                    .payload(DISABLE_WEB_PAGE_PREVIEW, true)
                    .build();
            sending.send(boxAnswer);

            sendNotice();
        }
    }

    /**
     * Используется для уведомления пользователя о выходе новой версии.
     */
    private void sendNotice() {
        final String requestUrl = "https://metrika.struchkov.dev/gitlab-notify/start-notice";
        final Request request = new Request.Builder()
                .get()
                .url(requestUrl)
                .build();
        try {
            final Response response = client.newCall(request).execute();
            if (response.code() == 200) {
                final String noticeMessage = response.body().string();
                if (checkNotBlank(noticeMessage)) {
                    final BoxAnswer notice = BoxAnswer.builder()
                            .message(noticeMessage)
                            .recipientPersonId(personProperty.getTelegramId())
//                            .payload(DISABLE_WEB_PAGE_PREVIEW, true)
                            .build();
                    final Optional<SentBox> optSentBox = sending.send(notice);
                    if (optSentBox.isPresent()) {
                        final SentBox sentBox = optSentBox.get();
                        final String messageId = sentBox.getMessageId();
                        telegramService.pinMessage(personProperty.getTelegramId(), messageId);
                    }
                }
            }
        } catch (IOException e) {
            log.warn(e.getMessage());
        }
    }

}
