package com.tsc.bitbucketbot.scheduler;

import com.tsc.bitbucketbot.domain.change.Change;
import com.tsc.bitbucketbot.domain.change.ConflictPrChange;
import com.tsc.bitbucketbot.domain.change.NewPrChange;
import com.tsc.bitbucketbot.domain.change.ReviewersPrChange;
import com.tsc.bitbucketbot.domain.change.StatusPrChange;
import com.tsc.bitbucketbot.domain.change.UpdatePrChange;
import com.tsc.bitbucketbot.exception.NotFoundException;
import com.tsc.bitbucketbot.service.ChangeService;
import com.tsc.bitbucketbot.service.MessageSendService;
import com.tsc.bitbucketbot.utils.Message;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SchedulerChangeParsing {

    private final MessageSendService messageSendService;
    private final ChangeService changeService;

    //    @Scheduled(cron = "0 * * * * *")
    @Scheduled(fixedRate = 5000)
    public void parsing() {
        final List<Change> newChange = changeService.getNew().stream()
                .filter(change -> change.getTelegramId() != null && !change.getTelegramId().isEmpty())
                .collect(Collectors.toList());
        for (Change change : newChange) {
            final String message = generateMessage(change);
            System.out.println(message);
//            change.getTelegramId().forEach(
//                    telegramId -> messageSendService.add(
//                            MessageSend.builder()
//                                    .telegramId(telegramId)
//                                    .message(message)
//                                    .build()
//                    )
//            );
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
            default:
                throw new NotFoundException("Нет обработчика для типа " + change.getType().name());
        }
        return message;
    }

}
