package org.sadtech.bot.bitbucketbot.scheduler;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.sadtech.bot.bitbucketbot.domain.MessageSend;
import org.sadtech.bot.bitbucketbot.domain.change.Change;
import org.sadtech.bot.bitbucketbot.domain.change.comment.AnswerCommentChange;
import org.sadtech.bot.bitbucketbot.domain.change.comment.CommentChange;
import org.sadtech.bot.bitbucketbot.domain.change.pullrequest.ConflictPrChange;
import org.sadtech.bot.bitbucketbot.domain.change.pullrequest.NewPrChange;
import org.sadtech.bot.bitbucketbot.domain.change.pullrequest.ReviewersPrChange;
import org.sadtech.bot.bitbucketbot.domain.change.pullrequest.StatusPrChange;
import org.sadtech.bot.bitbucketbot.domain.change.pullrequest.UpdatePrChange;
import org.sadtech.bot.bitbucketbot.domain.change.task.TaskChange;
import org.sadtech.bot.bitbucketbot.exception.NotFoundException;
import org.sadtech.bot.bitbucketbot.service.ChangeService;
import org.sadtech.bot.bitbucketbot.service.MessageSendService;
import org.sadtech.bot.bitbucketbot.utils.Message;
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
            final String message = generateMessage(change);
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

    /**
     * Создает сообщение, которое необходимо отправить в зависимости от типа изменения.
     *
     * @param change Объект изменения
     * @return Текстовое сообщение
     */
    private String generateMessage(@NonNull Change change) {
        String message;
        switch (change.getType()) {
            case NEW_PR:
                message = Message.generate(((NewPrChange) change));
                break;
            case REVIEWERS:
                message = Message.generate(((ReviewersPrChange) change));
                break;
            case STATUS_PR:
                message = Message.generate(((StatusPrChange) change));
                break;
            case UPDATE_PR:
                message = Message.generate(((UpdatePrChange) change));
                break;
            case CONFLICT_PR:
                message = Message.generate(((ConflictPrChange) change));
                break;
            case NEW_COMMENT:
                message = Message.generate(((CommentChange) change));
                break;
            case NEW_ANSWERS_COMMENT:
                message = Message.generate(((AnswerCommentChange) change));
                break;
            case NEW_TASK:
            case OPEN_TASK:
                message = Message.generateNewTask(((TaskChange) change));
                break;
            case DELETED_TASK:
                message = Message.generateDeleteTask(((TaskChange) change));
                break;
            case RESOLVED_TASK:
                message = Message.generateResolveTask(((TaskChange) change));
                break;
            default:
                throw new NotFoundException("Нет обработчика для типа " + change.getType().name());
        }
        return message;
    }

}
