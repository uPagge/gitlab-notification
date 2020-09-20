package org.sadtech.bot.vcs.core.scheduler;

import lombok.RequiredArgsConstructor;
import org.sadtech.bot.vcs.core.domain.MessageSend;
import org.sadtech.bot.vcs.core.domain.notify.Notify;
import org.sadtech.bot.vcs.core.service.NotifyService;
import org.sadtech.bot.vcs.core.service.MessageSendService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Парсер изменений. Отслеживает изменения, которые были добавлены и добавляет событие на отправку уведомления
 * пользователю.
 *
 * @author upagge
 */
@Service
@RequiredArgsConstructor
public class NotifyParsingScheduler {

    private final MessageSendService messageSendService;
    private final NotifyService notifyService;

    /**
     * Проверяет наличие новых изменений. Если изменения найдены, то создает новое сообщение и отправляет
     * его в сервис отправки сообщений {@link MessageSendService}
     */
    @Scheduled(cron = "*/15 * * * * *")
    public void parsing() {
        final List<Notify> newNotify = notifyService.getNew().stream()
                .filter(notify -> notify.getTelegramIds() != null && !notify.getTelegramIds().isEmpty())
                .collect(Collectors.toList());
        for (Notify notify : newNotify) {
            final String message = notify.generateMessage();
            notify.getTelegramIds().forEach(
                    telegramId -> messageSendService.add(
                            MessageSend.builder()
                                    .telegramId(telegramId)
                                    .message(message)
                                    .build()
                    )
            );
        }
    }

}
