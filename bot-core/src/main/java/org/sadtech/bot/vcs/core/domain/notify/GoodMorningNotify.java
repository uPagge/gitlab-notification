package org.sadtech.bot.vcs.core.domain.notify;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import org.sadtech.bot.vcs.core.domain.entity.PullRequest;
import org.sadtech.bot.vcs.core.utils.MessageUtils;
import org.sadtech.bot.vcs.core.utils.Smile;
import org.sadtech.bot.vcs.core.utils.UpdateDataComparator;

import java.util.List;
import java.util.Set;

/**
 * // TODO: 20.09.2020 Добавить описание.
 *
 * @author upagge 20.09.2020
 */
@Getter
public class GoodMorningNotify extends Notify {

    private static final UpdateDataComparator COMPARATOR = new UpdateDataComparator();
    private static final Integer PR_COUNT = 4;

    private final List<PullRequest> pullRequestsReviews;
    private final List<PullRequest> pullRequestsNeedWork;

    @Builder
    protected GoodMorningNotify(
            Set<String> logins,
            List<PullRequest> pullRequestsReviews,
            List<PullRequest> pullRequestsNeedWork
    ) {
        super(logins);
        this.pullRequestsReviews = pullRequestsReviews;
        this.pullRequestsNeedWork = pullRequestsNeedWork;
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

    private static String link(String name, String url) {
        return "[" + name + "](" + url + ")";
    }

    @Override
    public String generateMessage() {
        StringBuilder message = new StringBuilder().append(Smile.SUN).append(" *Доброе утро* ").append(Smile.SUN).append(Smile.HR);
        if (!pullRequestsReviews.isEmpty()) {
            message.append("Необходимо проверить ").append(pullRequestsReviews.size()).append(" ПР!").append(Smile.TWO_BR)
                    .append("Самые старые:").append(Smile.BR);
            MessageUtils.pullRequestForReview(pullRequestsReviews).ifPresent(message::append);
        } else {
            message.append("Ты либо самый лучший работник, либо тебе не доверяют проверку ПР ").append(Smile.MEGA_FUN).append(Smile.TWO_BR)
                    .append("Поздравляю, у тебя ни одного ПР на проверку!").append(Smile.BR);
        }
        if (!pullRequestsNeedWork.isEmpty()) {
            message.append(Smile.BR).append(Smile.DANGEROUS).append("Требуется доработать ").append(pullRequestsNeedWork.size()).append(" ПР:").append(Smile.BR);
            message.append(needWorkPr(pullRequestsNeedWork)).append(Smile.BR);
        }
        message
                .append(Smile.BR).append("Удачного дня ").append(Smile.FLOWER).append(Smile.TWO_BR);
        return message.toString();
    }

}
