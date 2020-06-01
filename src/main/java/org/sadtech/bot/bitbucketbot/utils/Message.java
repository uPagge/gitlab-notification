package org.sadtech.bot.bitbucketbot.utils;

import lombok.NonNull;
import org.sadtech.bot.bitbucketbot.domain.change.AnswerCommentChange;
import org.sadtech.bot.bitbucketbot.domain.change.CommentChange;
import org.sadtech.bot.bitbucketbot.domain.change.ConflictPrChange;
import org.sadtech.bot.bitbucketbot.domain.change.NewPrChange;
import org.sadtech.bot.bitbucketbot.domain.change.ReviewersPrChange;
import org.sadtech.bot.bitbucketbot.domain.change.StatusPrChange;
import org.sadtech.bot.bitbucketbot.domain.change.UpdatePrChange;
import org.sadtech.bot.bitbucketbot.domain.entity.PullRequest;
import org.sadtech.bot.bitbucketbot.domain.util.ReviewerChange;
import org.sadtech.bot.bitbucketbot.dto.bitbucket.CommentJson;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.sadtech.bot.bitbucketbot.domain.util.ReviewerChange.Type.DELETED;
import static org.sadtech.bot.bitbucketbot.domain.util.ReviewerChange.Type.NEW;
import static org.sadtech.bot.bitbucketbot.domain.util.ReviewerChange.Type.OLD;

/**
 * Генерирует сообщения для отправки.
 *
 * @author upagge [07.02.2020]
 */
public class Message {

    private static final UpdateDataComparator COMPARATOR = new UpdateDataComparator();
    private static final Integer PR_COUNT = 4;
    private static final String DONATION_LINK = "https://www.tinkoff.ru/sl/1T9s4esiMf";
    private static final String HELP_LINK = "https://nuzhnapomosh.ru/about/";

    private Message() {
        throw new IllegalStateException("Утилитарный класс");
    }

    @NonNull
    public static String generate(NewPrChange newPrChange) {
        String message = Smile.FUN + " *Новый Pull Request*" + Smile.BR +
                link(newPrChange.getName(), newPrChange.getUrl()) +
                Smile.HR;
        if (newPrChange.getDescription() != null && !"".equals(newPrChange.getDescription())) {
            message += newPrChange.getDescription() + Smile.HR;
        }
        message += Smile.AUTHOR + ": " + newPrChange.getAuthor() + Smile.TWO_BR;
        return message;
    }

    public static String generate(@NonNull StatusPrChange change) {
        return Smile.PEN + " *Изменился статус вашего ПР*" + Smile.HR +
                link(change.getName(), change.getUrl()) + Smile.BR +
                change.getOldStatus().name() + " -> " + change.getNewStatus().name() +
                Smile.TWO_BR;
    }

    @NonNull
    public static String generate(@NonNull ReviewersPrChange reviewersChange) {
        StringBuilder stringBuilder = new StringBuilder();
        final Map<ReviewerChange.Type, List<ReviewerChange>> changes = reviewersChange.getReviewerChanges().stream()
                .collect(Collectors.groupingBy(ReviewerChange::getType));
        if (changes.containsKey(OLD)) {
            stringBuilder.append(Smile.BR).append("Изменили свое решение:").append(Smile.BR);
            changes.get(OLD).forEach(
                    change -> stringBuilder
                            .append(Smile.AUTHOR).append(change.getName()).append(": ")
                            .append(change.getOldStatus().getValue()).append(" -> ")
                            .append(change.getStatus().getValue())
                            .append(Smile.BR)
            );
        }
        if (changes.containsKey(NEW)) {
            stringBuilder.append(Smile.BR).append("Новые ревьюверы:").append(Smile.BR);
            changes.get(NEW).forEach(
                    change -> stringBuilder
                            .append(change.getName()).append(" (").append(change.getStatus().getValue()).append(")")
                            .append(Smile.BR)
            );
        }
        if (changes.containsKey(DELETED)) {
            stringBuilder.append(Smile.BR).append("Не выдержали ревью:").append(Smile.BR)
                    .append(
                            changes.get(DELETED).stream()
                                    .map(ReviewerChange::getName).collect(Collectors.joining(","))
                    );
        }
        final String createMessage = stringBuilder.toString();
        return Smile.PEN + " *Изменения ревьюверов вашего ПР*" +
                Smile.HR +
                link(reviewersChange.getName(), reviewersChange.getUrl()) + Smile.BR +
                createMessage;
    }

    public static String generate(@NonNull UpdatePrChange change) {
        return Smile.UPDATE + " *Обновление Pull Request*" + Smile.BR +
                link(change.getName(), change.getUrl()) +
                Smile.HR +
                Smile.AUTHOR + ": " + change.getAuthor() +
                Smile.TWO_BR;
    }

    public static String generate(@NonNull ConflictPrChange change) {
        return Smile.DANGEROUS + "*Внимание конфликт в ПР*" + Smile.HR +
                link(change.getName(), change.getUrl()) + Smile.TWO_BR;
    }

    @NonNull
    public static String goodMorningStatistic(List<PullRequest> pullRequestsReviews, List<PullRequest> pullRequestsNeedWork) {
        StringBuilder message = new StringBuilder().append(Smile.SUN).append(" Доброе утро ").append(Smile.SUN).append(Smile.HR);
        if (!pullRequestsReviews.isEmpty()) {
            message.append("Сегодня тебя ждет проверка ").append(pullRequestsReviews.size()).append(" ПР!").append(Smile.TWO_BR)
                    .append("Самые старые:").append(Smile.BR);
            List<PullRequest> oldPr = pullRequestsReviews.stream()
                    .sorted(COMPARATOR)
                    .limit(PR_COUNT)
                    .collect(Collectors.toList());
            oldPr.forEach(pullRequest -> message.append(topPr(pullRequest)));
        } else {
            message.append("Ты либо самый лучший работник, либо тебе не доверяют проверку ПР ").append(Smile.MEGA_FUN).append(Smile.TWO_BR)
                    .append("Поздравляю, у тебя ни одного ПР на проверку!").append(Smile.BR);
        }
        if (!pullRequestsNeedWork.isEmpty()) {
            message.append(Smile.BR).append(Smile.DANGEROUS).append("Требуется доработать ").append(pullRequestsNeedWork.size()).append(" ПР:").append(Smile.BR);
            message.append(needWorkPr(pullRequestsNeedWork)).append(Smile.BR);
        }
        if (dayX()) {
            message.append(Smile.BR).append(Smile.FUN).append(" Кстати, поздравляю, сегодня день З/П").append(Smile.BR)
                    .append(Smile.DANGEROUS).append("Спасибо всем, кто ").append(link("донатил", DONATION_LINK)).append(", мы оплатили хостинг до октября :)")
                    .append(Smile.BR).append("Теперь стоит ").append(link("помочь", HELP_LINK)).append(" тем, кто действительно в этом нуждается))")
                    .append(Smile.BR);
        }
        message
                .append(Smile.BR)
                .append("Удачного дня ").append(Smile.FLOWER).append(Smile.TWO_BR);
        return message.toString();
    }

    @NonNull
    public static String goodWeekEnd() {
        return "Ну вот и все! Веселых выходных " + Smile.MIG + Smile.BR +
                "До понедельника" + Smile.BUY + Smile.TWO_BR;
    }

    public static String commentPr(@NonNull CommentJson comment, @NonNull String namePr, @NonNull String urlPr) {
        return Smile.BELL + " *Новый комментарий к ПР*" + Smile.BR +
                link(namePr, urlPr) +
                Smile.HR +
                comment.getAuthor().getName() + ": " + comment.getText().replaceAll("@[\\w]+", "");
    }

    public static String generate(@NonNull CommentChange commentChange) {
        return Smile.BELL + " *Новое упоминание* | " + link("ПР", commentChange.getUrl()) + Smile.HR +
                commentChange.getAuthorName() + ": " + commentChange.getMessage().replaceAll("@[\\w]+", "");
    }

    public static String generate(@NonNull AnswerCommentChange answerCommentChange) {
        final StringBuilder message = new StringBuilder();
        message.append(Smile.BELL).append("Новые ответы на ваш комментарий").append(" | ").append(link("ПР", answerCommentChange.getUrl())).append(Smile.HR)
                .append(answerCommentChange.getYouMessage(), 0, Math.min(answerCommentChange.getYouMessage().length(), 180)).append(Smile.HR);
        answerCommentChange.getAnswers().forEach(answer -> message.append(answer.getAuthorName()).append(": ")
                .append(answer.getMessage(), 0, Math.min(answer.getMessage().length(), 500)).append(Smile.TWO_BR));
        return message.toString();
    }

    private static String needWorkPr(@NonNull List<PullRequest> pullRequestsNeedWork) {
        final StringBuilder message = new StringBuilder();
        pullRequestsNeedWork.stream()
                .limit(3)
                .forEach(
                        pullRequest -> message.append("-- ").append(link(pullRequest.getName(), pullRequest.getUrl())).append(Smile.BR)
                );
        return message.toString();
    }

    private static String topPr(PullRequest pullRequest) {
        return Smile.statusPr(pullRequest.getUpdateDate()) + " " +
                link(pullRequest.getName(), pullRequest.getUrl()) +
                Smile.BR;
    }

    private static boolean dayX() {
        int dayOfMonth = LocalDate.now().getDayOfMonth();
        return dayOfMonth == 20 || dayOfMonth == 5;
    }

    @NonNull
    private static String link(String name, String url) {
        return "[" + name + "](" + url + ")";
    }

}
