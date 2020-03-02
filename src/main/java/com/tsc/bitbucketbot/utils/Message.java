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
 * Генерирует сообщения для отправки.
 *
 * @author upagge [07.02.2020]
 */
public class Message {

    public static final String EMPTY = "";
    private static final String BREAK = "\n";
    private static final String TWO_BREAK = "\n\n";
    private static final String SMILE_AUTHOR = "\uD83D\uDC68\u200D\uD83D\uDCBB️";
    private static final String SMILE_PEN = "✏️";
    private static final String SMILE_UPDATE = "\uD83D\uDD04";
    private static final String HR = "\n -- -- -- -- --\n";

    private Message() {
        throw new IllegalStateException("Утилитарный класс");
    }

    @NonNull
    public static String newPullRequest(PullRequest pullRequest) {
        return "\uD83C\uDF89 *Новый Pull Request*\n" +
                "[" + pullRequest.getName() + "](" + pullRequest.getUrl() + ")\n" +
                HR +
                SMILE_AUTHOR + ": " + pullRequest.getAuthor().getLogin() +
                TWO_BREAK;
    }

    @NonNull
    public static String statusPullRequest(String name, String url, PullRequestStatus oldStatus, PullRequestStatus newStatus) {
        return SMILE_PEN + " *Изменился статус вашего ПР*" + HR +
                "[" + name + "](" + url + ")" + BREAK +
                oldStatus.name() + " -> " + newStatus.name() +
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
                    SMILE_PEN + " *Изменения ревьюверов вашего ПР*" +
                            HR +
                            "[" + pullRequest.getName() + "](" + pullRequest.getUrl() + ")" + BREAK +
                            createMessage
            );
        }
        return Optional.empty();
    }

    @NonNull
    public static String updatePullRequest(String pullRequestName, String prUrl, String author) {
        return SMILE_UPDATE + " *Обновление Pull Request*" + BREAK +
                "[" + pullRequestName + "](" + prUrl + ")" +
                HR +
                SMILE_AUTHOR + ": " + author +
                TWO_BREAK;
    }
}
