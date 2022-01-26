package dev.struchkov.bot.gitlab.telegram.scheduler;

import dev.struchkov.godfather.telegram.autoresponder.MessageAutoresponderTelegram;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CheckNewMessage {

    private final MessageAutoresponderTelegram messageAutoresponderTelegram;

    @Scheduled(fixedDelay = 5000)
    public void check() {
        messageAutoresponderTelegram.checkNewMessage();
    }

}
