package org.sadtech.bot.gitlab.context.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.sadtech.bot.gitlab.context.domain.entity.PullRequest;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Генерирует сообщения для отправки.
 *
 * @author upagge [07.02.2020]
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class MessageUtils {

    private static final UpdateDataComparator COMPARATOR = new UpdateDataComparator();

    public static Optional<String> pullRequestForReview(@NonNull List<PullRequest> pullRequestsReviews) {
        if (!pullRequestsReviews.isEmpty()) {
            return Optional.of(
                    pullRequestsReviews.stream()
                            .sorted(COMPARATOR)
                            .map(MessageUtils::generateStringItemPullRequestReview)
                            .collect(Collectors.joining("\n"))
            );
        }
        return Optional.empty();
    }

    public static Optional<String> pullRequestForNeedWork(@NonNull List<PullRequest> pullRequestNeedWork) {
        if (!pullRequestNeedWork.isEmpty()) {
            return Optional.of(
                    pullRequestNeedWork.stream()
                            .map(MessageUtils::generateStringItemPullRequestNeedWork)
                            .collect(Collectors.joining("\n"))
            );
        }
        return Optional.empty();
    }

    private static String generateStringItemPullRequestNeedWork(PullRequest pullRequest) {
        return "-- " + link(pullRequest.getTitle(), pullRequest.getUrl());
    }

    private static String generateStringItemPullRequestReview(PullRequest pullRequest) {
        return Smile.statusPr(pullRequest.getUpdateDate()) + " " +
                link(pullRequest.getTitle(), pullRequest.getUrl());
    }

    @NonNull
    private static String link(String name, String url) {
        return "[" + name + "](" + url + ")";
    }

}
