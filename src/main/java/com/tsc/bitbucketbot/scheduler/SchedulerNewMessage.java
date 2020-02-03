package com.tsc.bitbucketbot.scheduler;

import lombok.RequiredArgsConstructor;
import org.sadtech.telegram.bot.autoresponder.MessageAutoresponderTelegram;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * TODO: Добавить описание класса.
 *
 * @author upagge [30.01.2020]
 */
@Service
@RequiredArgsConstructor
public class SchedulerNewMessage {

    private final MessageAutoresponderTelegram messageAutoresponderTelegram;

    @Scheduled(fixedRate = 3000)
    public void scan() {
        messageAutoresponderTelegram.checkNewMessage();
    }

}
