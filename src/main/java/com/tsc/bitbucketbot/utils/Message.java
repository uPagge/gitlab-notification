package com.tsc.bitbucketbot.utils;

import com.tsc.bitbucketbot.domain.PullRequestStatus;
import com.tsc.bitbucketbot.domain.entity.PullRequest;
import com.tsc.bitbucketbot.domain.util.ReviewerChange;
import lombok.NonNull;

import java.time.LocalDate;
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

    private static final UpdateDataComparator COMPARATOR = new UpdateDataComparator();
    private static final Integer PR_COUNT = 4;
    private static final String DONATION_LINK = "https://www.tinkoff.ru/sl/1T9s4esiMf";

    private Message() {
        throw new IllegalStateException("Утилитарный класс");
    }

    @NonNull
    public static String newPullRequest(PullRequest pullRequest) {
        return Smile.FUN + " *Новый Pull Request*" + Smile.BREAK +
                link(pullRequest.getName(), pullRequest.getUrl()) +
                Smile.HR +
                Smile.AUTHOR + ": " + pullRequest.getAuthor().getLogin() +
                Smile.TWO_BREAK;
    }

    @NonNull
    public static String statusPullRequest(String name, String url, PullRequestStatus oldStatus, PullRequestStatus newStatus) {
        return Smile.PEN + " *Изменился статус вашего ПР*" + Smile.HR +
                link(name, url) + Smile.BREAK +
                oldStatus.name() + " -> " + newStatus.name() +
                Smile.TWO_BREAK;
    }

    @NonNull
    public static Optional<String> statusReviewers(PullRequest pullRequest, List<ReviewerChange> reviewerChanges) {
        StringBuilder stringBuilder = new StringBuilder();
        final Map<ReviewerChange.Type, List<ReviewerChange>> changes = reviewerChanges.stream()
                .collect(Collectors.groupingBy(ReviewerChange::getType));
        if (changes.containsKey(OLD)) {
            stringBuilder.append(Smile.BREAK).append("Изменили свое решение:").append(Smile.BREAK);
            changes.get(OLD).forEach(
                    change -> stringBuilder
                            .append(Smile.AUTHOR).append(change.getName()).append(": ")
                            .append(change.getOldStatus().getValue()).append(" -> ")
                            .append(change.getStatus().getValue())
                            .append(Smile.BREAK)
            );
        }
        if (changes.containsKey(NEW)) {
            stringBuilder.append(Smile.BREAK).append("Новые ревьюверы:").append(Smile.BREAK);
            changes.get(NEW).forEach(
                    change -> stringBuilder
                            .append(change.getName()).append(" (").append(change.getStatus().getValue()).append(")")
                            .append(Smile.BREAK)
            );
        }
        if (changes.containsKey(DELETED)) {
            stringBuilder.append(Smile.BREAK).append("Не выдержали ревью:").append(Smile.BREAK)
                    .append(
                            changes.get(DELETED).stream()
                                    .map(ReviewerChange::getName).collect(Collectors.joining(","))
                    );
        }

        final String createMessage = stringBuilder.toString();
        if (!Smile.Constants.EMPTY.equalsIgnoreCase(createMessage)) {
            return Optional.of(
                    Smile.PEN + " *Изменения ревьюверов вашего ПР*" +
                            Smile.HR +
                            link(pullRequest.getName(), pullRequest.getUrl()) + Smile.BREAK +
                            createMessage
            );
        }
        return Optional.empty();
    }

    @NonNull
    public static String updatePullRequest(String pullRequestName, String prUrl, String author) {
        return Smile.UPDATE + " *Обновление Pull Request*" + Smile.BREAK +
                link(pullRequestName, prUrl) +
                Smile.HR +
                Smile.AUTHOR + ": " + author +
                Smile.TWO_BREAK;
    }

    @NonNull
    public static String goodMorningStatistic(List<PullRequest> pullRequestsReviews, List<PullRequest> pullRequestsNeedWork) {
        StringBuilder message = new StringBuilder().append(Smile.SUN).append(" Доброе утро ").append(Smile.SUN).append(Smile.HR);
        if (!pullRequestsReviews.isEmpty()) {
            message.append("Сегодня тебя ждет проверка ").append(pullRequestsReviews.size()).append(" ПР!").append(Smile.TWO_BREAK)
                    .append("Топ старых ПР:").append(Smile.BREAK);
            List<PullRequest> oldPr = pullRequestsReviews.stream()
                    .sorted(COMPARATOR)
                    .limit(PR_COUNT)
                    .collect(Collectors.toList());
            oldPr.forEach(pullRequest -> message.append(topPr(pullRequest)));
            message.append(Smile.BREAK);
        } else {
            message.append("Ты либо самый лучший работник, либо тебе не доверяют проверку ПР ").append(Smile.MEGA_FUN).append(Smile.TWO_BREAK)
                    .append("Поздравляю, у тебя ни одного ПР на проверку!").append(Smile.BREAK);
        }
        if (!pullRequestsNeedWork.isEmpty()) {
            message.append(Smile.BREAK).append(Smile.DANGEROUS).append(" Так же у тебя на доработке находится ").append(pullRequestsNeedWork.size()).append(" ПР").append(Smile.BREAK);
            message.append(needWorkPr(pullRequestsNeedWork)).append(Smile.BREAK);
        }
        if (dayX()) {
            message.append(Smile.BREAK).append(Smile.FUN).append(" Кстати, поздравляю, сегодня день З/П").append(Smile.BREAK)
                    .append(Smile.DANGEROUS).append("И раз такое дело, то напоминаю, что в виду независящих от разработчика условий, бот работает на платном VDS. Поэтому всячески приветствуются ")
                    .append(link("донаты на оплату сервера", DONATION_LINK)).append(Smile.BREAK);
        }
        message
                .append(Smile.BREAK)
                .append("Удачного дня ").append(Smile.FLOWER).append(Smile.TWO_BREAK);
        return message.toString();
    }

    private static String needWorkPr(@NonNull List<PullRequest> pullRequestsNeedWork) {
        final StringBuilder message = new StringBuilder();
        pullRequestsNeedWork.stream()
                .limit(3)
                .forEach(
                        pullRequest -> message.append("-- ").append(link(pullRequest.getName(), pullRequest.getUrl())).append(Smile.BREAK)
                );
        return message.toString();
    }

    private static String topPr(PullRequest pullRequest) {
        return Smile.statusPr(pullRequest.getUpdateDate()) + " " +
                link(pullRequest.getName(), pullRequest.getUrl()) +
                Smile.BREAK;
    }

    private static boolean dayX() {
        int dayOfMonth = LocalDate.now().getDayOfMonth();
        return dayOfMonth == 20 || dayOfMonth == 5;
    }

    @NonNull
    private static String link(String name, String url) {
        return "[" + name + "](" + url + ")";
    }

    @NonNull
    public static String goodWeekEnd() {
        return "Ну вот и все! Веселых выходных " + Smile.MIG + Smile.BREAK +
                "До понедельника" + Smile.BUY + Smile.TWO_BREAK;
    }
}
