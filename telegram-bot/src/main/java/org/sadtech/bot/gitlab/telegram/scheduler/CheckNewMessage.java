package org.sadtech.bot.gitlab.telegram.scheduler;

import lombok.RequiredArgsConstructor;
import org.sadtech.bot.godfather.telegram.autoresponder.MessageAutoresponderTelegram;
import org.springframework.scheduling.annotation.Scheduled;

//@Service
@RequiredArgsConstructor
public class CheckNewMessage {

    private final MessageAutoresponderTelegram messageAutoresponderTelegram;

    @Scheduled(fixedDelay = 5000)
    public void check() {
        messageAutoresponderTelegram.checkNewMessage();
    }

}
