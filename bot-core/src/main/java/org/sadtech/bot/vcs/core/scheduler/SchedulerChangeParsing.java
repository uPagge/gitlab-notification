package org.sadtech.bot.vcs.core.scheduler;

import lombok.RequiredArgsConstructor;
import org.sadtech.bot.vcs.core.domain.MessageSend;
import org.sadtech.bot.vcs.core.domain.change.Change;
import org.sadtech.bot.vcs.core.service.ChangeService;
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
public class SchedulerChangeParsing {

    private final MessageSendService messageSendService;
    private final ChangeService changeService;

    /**
     * Проверяет наличие новых изменений. Если изменения найдены, то создает новое сообщение и отправляет
     * его в сервис отправки сообщений {@link MessageSendService}
     */
    @Scheduled(cron = "*/15 * * * * *")
    public void parsing() {
        final List<Change> newChange = changeService.getNew().stream()
                .filter(change -> change.getTelegramIds() != null && !change.getTelegramIds().isEmpty())
                .collect(Collectors.toList());
        for (Change change : newChange) {
            final String message = change.generateMessage();
            change.getTelegramIds().forEach(
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
