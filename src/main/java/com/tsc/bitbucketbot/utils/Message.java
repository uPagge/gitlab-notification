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
    public static final String BREAK = "\n";
    public static final String TWO_BREAK = "\n\n";
    public static final String SMILE_AUTHOR = "\uD83D\uDC68\u200D\uD83D\uDCBB️";
    public static final String SMILE_PEN = "✏️";
    public static final String SMILE_NEW_PR = "\uD83C\uDF89";
    public static final String SMILE_UPDATE = "\uD83D\uDD04";
    public static final String SMILE_SUN = "\uD83D\uDD06";
    public static final String SMILE_PIN = "\uD83D\uDCCD";
    public static final String HR = "\n -- -- -- -- --\n";

    private Message() {
        throw new IllegalStateException("Утилитарный класс");
    }

    @NonNull
    public static String newPullRequest(PullRequest pullRequest) {
        return SMILE_NEW_PR + " *Новый Pull Request*" + BREAK +
                "[" + pullRequest.getName() + "](" + pullRequest.getUrl() + ")" +
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

    @NonNull
    public static String goodMorningStatistic(String userName, List<PullRequest> pullRequests) {
        StringBuilder message = new StringBuilder(SMILE_SUN).append(" Доброе утро, ").append(userName).append("!")
                .append(HR);
        if (!pullRequests.isEmpty()) {
            message.append("Сегодня тебя ждет процерка целых ").append(pullRequests.size()).append(" ПР!").append(TWO_BREAK)
                    .append("Позволь представить, горячая десятка:").append(BREAK);
            pullRequests.stream()
                    .sorted(new UpdateDataComparator())
                    .limit(10)
                    .forEach(pullRequest -> message.append(SMILE_PIN)
                            .append("[").append(pullRequest.getName()).append("](").append(pullRequest.getUrl()).append(")").append(BREAK));
        } else {
            message.append("Ты либо самый лучший рабоник, либо тебе не доверяют проверку ПР :D").append(BREAK)
                    .append("Поздравляю, у тебя ни одного ПР на проверку!").append(BREAK);
        }
        return message
                .append(BREAK)
                .append("Удачной работы сегодня!")
                .toString();
    }


}
