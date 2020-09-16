package org.sadtech.bot.bitbucketbot.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.sadtech.bot.bitbucketbot.domain.entity.PullRequest;
import org.sadtech.bot.bitbucketbot.dto.bitbucket.CommentJson;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Генерирует сообщения для отправки.
 *
 * @author upagge [07.02.2020]
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Message {

    private static final UpdateDataComparator COMPARATOR = new UpdateDataComparator();
    private static final Integer PR_COUNT = 4;
    private static final String DONATION_LINK = "https://www.tinkoff.ru/sl/1T9s4esiMf";

    @NonNull
    public static String goodMorningStatistic(List<PullRequest> pullRequestsReviews, List<PullRequest> pullRequestsNeedWork) {
        StringBuilder message = new StringBuilder().append(Smile.SUN).append(" *Доброе утро* ").append(Smile.SUN).append(Smile.HR);
        if (!pullRequestsReviews.isEmpty()) {
            message.append("Необходимо проверить ").append(pullRequestsReviews.size()).append(" ПР!").append(Smile.TWO_BR)
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
                    .append(Smile.DANGEROUS).append("Хостинг оплачен до 5 октября! Прошу неравнодушных ").append(link("задонатить", DONATION_LINK))
                    .append(Smile.BR);
        }
        message
                .append(Smile.BR).append("Удачного дня ").append(Smile.FLOWER).append(Smile.TWO_BR);
        return message.toString();
    }

    @NonNull
    public static String goodWeekEnd() {
        return "Ну вот и все! Веселых выходных " + Smile.MIG + Smile.BR +
                "До понедельника" + Smile.BUY + Smile.TWO_BR;
    }

    public static String commentPr(@NonNull CommentJson comment, @NonNull String namePr, @NonNull String urlPr) {
        return Smile.BELL + " *Новый комментарий к ПР*" + Smile.BR +
                link(namePr, urlPr + "/overview?commentId=" + comment.getId()) +
                Smile.HR +
                comment.getAuthor().getName() + ": " + comment.getText().replaceAll("@[\\w]+", "");
    }

    private static String needWorkPr(@NonNull List<PullRequest> pullRequestsNeedWork) {
        final StringBuilder message = new StringBuilder();
        pullRequestsNeedWork.stream()
                .limit(3)
                .forEach(
                        pullRequest -> message.append("-- ").append(link(pullRequest.getTitle(), pullRequest.getUrl())).append(Smile.BR)
                );
        return message.toString();
    }

    private static String topPr(PullRequest pullRequest) {
        return Smile.statusPr(pullRequest.getUpdateDate()) + " " +
                link(pullRequest.getTitle(), pullRequest.getUrl()) +
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
