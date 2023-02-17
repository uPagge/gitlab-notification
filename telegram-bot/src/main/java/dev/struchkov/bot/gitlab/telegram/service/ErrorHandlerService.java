package dev.struchkov.bot.gitlab.telegram.service;

import dev.struchkov.bot.gitlab.core.config.properties.PersonProperty;
import dev.struchkov.godfather.main.domain.content.Message;
import dev.struchkov.godfather.simple.context.service.ErrorHandler;
import dev.struchkov.godfather.simple.domain.BoxAnswer;
import dev.struchkov.godfather.telegram.simple.context.service.TelegramSending;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static dev.struchkov.godfather.simple.domain.BoxAnswer.boxAnswer;


@Slf4j
@Service
@RequiredArgsConstructor
public class ErrorHandlerService implements ErrorHandler {

    private final PersonProperty personProperty;
    private final TelegramSending telegramSending;

    @Override
    public void handle(Message message, Exception e) {
        log.error(e.getMessage(), e);
        final BoxAnswer boxAnswer = boxAnswer(e.getMessage());
        boxAnswer.setRecipientIfNull(personProperty.getTelegramId());
        telegramSending.send(boxAnswer);
    }

}
