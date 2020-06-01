package org.sadtech.bot.bitbucketbot.scheduler;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.sadtech.bot.bitbucketbot.domain.MessageSend;
import org.sadtech.bot.bitbucketbot.domain.change.AnswerCommentChange;
import org.sadtech.bot.bitbucketbot.domain.change.Change;
import org.sadtech.bot.bitbucketbot.domain.change.CommentChange;
import org.sadtech.bot.bitbucketbot.domain.change.ConflictPrChange;
import org.sadtech.bot.bitbucketbot.domain.change.NewPrChange;
import org.sadtech.bot.bitbucketbot.domain.change.ReviewersPrChange;
import org.sadtech.bot.bitbucketbot.domain.change.StatusPrChange;
import org.sadtech.bot.bitbucketbot.domain.change.UpdatePrChange;
import org.sadtech.bot.bitbucketbot.exception.NotFoundException;
import org.sadtech.bot.bitbucketbot.service.ChangeService;
import org.sadtech.bot.bitbucketbot.service.MessageSendService;
import org.sadtech.bot.bitbucketbot.utils.Message;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SchedulerChangeParsing {

    private final MessageSendService messageSendService;
    private final ChangeService changeService;

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
            default:
                throw new NotFoundException("Нет обработчика для типа " + change.getType().name());
        }
        return message;
    }

}
