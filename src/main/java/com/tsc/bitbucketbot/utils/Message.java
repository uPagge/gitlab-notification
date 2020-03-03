package com.tsc.bitbucketbot.utils;

import com.tsc.bitbucketbot.domain.PullRequestStatus;
import com.tsc.bitbucketbot.domain.entity.PullRequest;
import com.tsc.bitbucketbot.domain.util.ReviewerChange;
import lombok.NonNull;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
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
    private static final String SMILE_FUN = "\uD83C\uDF89";
    private static final String SMILE_UPDATE = "\uD83D\uDD04";
    private static final String SMILE_SUN = "\uD83D\uDD06";
    private static final String SMILE_MIG = "\uD83D\uDE09";
    private static final String SMILE_BUY = "\uD83D\uDC4B";
    private static final String SMILE_FLOWER = "\uD83C\uDF40";
    private static final String SMILE_DAY_0 = "\uD83C\uDF15";
    private static final String SMILE_DAY_1 = "\uD83C\uDF16";
    private static final String SMILE_DAY_2 = "\uD83C\uDF17";
    private static final String SMILE_DAY_3 = "\uD83C\uDF18";
    private static final String SMILE_DAY_4 = "\uD83C\uDF11";
    private static final String SMILE_DAY_5 = "\uD83C\uDF1A";
    private static final String SMILE_MEGA_FUN = "\uD83D\uDE02";
    private static final String HR = "\n -- -- -- -- --\n";

    private static final UpdateDataComparator COMPARATOR = new UpdateDataComparator();
    private static final Integer PR_COUNT = 4;

    private Message() {
        throw new IllegalStateException("Утилитарный класс");
    }

    @NonNull
    public static String newPullRequest(PullRequest pullRequest) {
        return SMILE_FUN + " *Новый Pull Request*" + BREAK +
                linkPr(pullRequest.getName(), pullRequest.getUrl()) +
                HR +
                SMILE_AUTHOR + ": " + pullRequest.getAuthor().getLogin() +
                TWO_BREAK;
    }

    @NonNull
    public static String statusPullRequest(String name, String url, PullRequestStatus oldStatus, PullRequestStatus newStatus) {
        return SMILE_PEN + " *Изменился статус вашего ПР*" + HR +
                linkPr(name, url) + BREAK +
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
                            linkPr(pullRequest.getName(), pullRequest.getUrl()) + BREAK +
                            createMessage
            );
        }
        return Optional.empty();
    }

    @NonNull
    public static String updatePullRequest(String pullRequestName, String prUrl, String author) {
        return SMILE_UPDATE + " *Обновление Pull Request*" + BREAK +
                linkPr(pullRequestName, prUrl) +
                HR +
                SMILE_AUTHOR + ": " + author +
                TWO_BREAK;
    }

    @NonNull
    public static String goodMorningStatistic(List<PullRequest> pullRequests) {
        StringBuilder message = new StringBuilder(SMILE_SUN).append(" Доброе утро ").append(SMILE_SUN).append(HR);
        if (!pullRequests.isEmpty()) {
            message.append("Сегодня тебя ждет проверка целых ").append(pullRequests.size()).append(" ПР!").append(TWO_BREAK)
                    .append("Топ старых ПР:").append(BREAK);
            List<PullRequest> oldPr = pullRequests.stream()
                    .sorted(COMPARATOR)
                    .limit(PR_COUNT)
                    .collect(Collectors.toList());
            oldPr.forEach(
                    pullRequest -> message.append(topPr(pullRequest))
            );
            Set<Long> oldPrIds = oldPr.stream().map(PullRequest::getId).collect(Collectors.toSet());
            if (pullRequests.size() > PR_COUNT) {
                message.append(BREAK).append("Свежие ПР:").append(BREAK);
                pullRequests
                        .stream()
                        .filter(pullRequest -> !oldPrIds.contains(pullRequest.getId()))
                        .sorted(COMPARATOR.reversed())
                        .limit(PR_COUNT)
                        .forEach(
                                pullRequest -> message.append(topPr(pullRequest))
                        );
            }
        } else {
            message.append("Ты либо самый лучший работник, либо тебе не доверяют проверку ПР ").append(SMILE_MEGA_FUN).append(TWO_BREAK)
                    .append("Поздравляю, у тебя ни одного ПР на проверку!").append(BREAK);
        }
        if (dayX()) {
            message.append(BREAK).append(SMILE_FUN).append(" Кстати, поздравляю, сегодня день З/П").append(BREAK);
        }
        message
                .append(BREAK)
                .append("Удачного дня ").append(SMILE_FLOWER).append(TWO_BREAK);

        return message.toString();
    }

    private static String topPr(PullRequest pullRequest) {
        return selectSmile(pullRequest) + " " +
                linkPr(pullRequest.getName(), pullRequest.getUrl()) +
                BREAK;
    }

    private static String selectSmile(PullRequest pullRequest) {
        switch (Period.between(LocalDate.now(), pullRequest.getUpdateDate()).getDays()) {
            case 0:
                return SMILE_DAY_0;
            case 1:
                return SMILE_DAY_1;
            case 2:
                return SMILE_DAY_2;
            case 3:
                return SMILE_DAY_3;
            case 4:
                return SMILE_DAY_4;
            default:
                return SMILE_DAY_5;
        }
    }

    private static boolean dayX() {
        int dayOfMonth = LocalDate.now().getDayOfMonth();
        return dayOfMonth == 20 || dayOfMonth == 5;
    }

    @NonNull
    private static String linkPr(String name, String url) {
        return "[" + name + "](" + url + ")";
    }

    @NonNull
    public static String goodWeekEnd() {
        return "Ну вот и все! Веселых выходных " + SMILE_MIG + BREAK +
                "До понедельника" + SMILE_BUY + TWO_BREAK;
    }
}
