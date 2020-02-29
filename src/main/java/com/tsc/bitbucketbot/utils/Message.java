package com.tsc.bitbucketbot.utils;

import com.tsc.bitbucketbot.domain.PullRequestStatus;
import com.tsc.bitbucketbot.domain.entity.PullRequest;
import com.tsc.bitbucketbot.domain.util.ReviewerChange;
import lombok.NonNull;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.tsc.bitbucketbot.domain.util.ReviewerChange.Type.*;

/**
 * TODO: Добавить описание класса.
 *
 * @author upagge [07.02.2020]
 */
public class Message {

    public static final String EMPTY = "";
    private static final String BREAK = "\n";
    private static final String TWO_BREAK = "\n\n";
    private static final String SMILE_AUTHOR = "\uD83D\uDC68\u200D\uD83D\uDCBB️";
    private static final String SMILE_PEN = "✏️";

    private Message() {
        throw new IllegalStateException("Утилитарный класс");
    }

    @NonNull
    public static String statusPullRequest(String name, String url, PullRequestStatus oldStatus, PullRequestStatus newStatus) {
        return "✏️ *Изменился статус вашего ПР*" + BREAK +
                "[" + name + "](" + url.replace("localhost", "192.168.236.164") + ")" + BREAK +
                oldStatus.name() + " -> " + newStatus.name() +
                BREAK + "-- -- -- --" + BREAK +
                "\uD83D\uDCCC: #pullRequest #change" +
                TWO_BREAK;
    }

    @NonNull
    public static Optional<String> statusReviewers(PullRequest pullRequest, List<ReviewerChange> reviewerChanges) {
        StringBuilder stringBuilder = new StringBuilder();
        final Map<ReviewerChange.Type, List<ReviewerChange>> changes = reviewerChanges.stream()
                .collect(Collectors.groupingBy(ReviewerChange::getType));
        if (changes.containsKey(OLD)) {
            stringBuilder.append(BREAK).append("Изменили свое решение:").append(BREAK);
            changes.get(OLD).forEach(
                    change -> stringBuilder
                            .append(SMILE_AUTHOR).append(change.getName()).append(": ")
                            .append(change.getOldStatus().getValue()).append(" -> ")
                            .append(change.getStatus().getValue())
                            .append(BREAK)
            );
        }
        if (changes.containsKey(NEW)) {
            stringBuilder.append(BREAK).append("Новые ревьюверы:").append(BREAK);
            changes.get(NEW).forEach(
                    change -> stringBuilder
                            .append(change.getName()).append(" (").append(change.getStatus().getValue()).append(")")
                            .append(BREAK)
            );
        }
        if (changes.containsKey(DELETED)) {
            stringBuilder.append(BREAK).append("Не выдержали ревью:").append(BREAK)
                    .append(
                            changes.get(DELETED).stream()
                                    .map(ReviewerChange::getName).collect(Collectors.joining(","))
                    );
        }

        final String createMessage = stringBuilder.toString();
        if (!EMPTY.equalsIgnoreCase(createMessage)) {
            return Optional.of(
                    SMILE_PEN + " *Изменения ревьюверов вашего ПР*" + BREAK +
                            "[" + pullRequest.getName() + "](" + pullRequest.getUrl().replace("localhost", "192.168.236.164") + ")" + BREAK +
                            createMessage
                            + "\n-- -- -- -- --"
            );
        }
        return Optional.empty();
    }


}
