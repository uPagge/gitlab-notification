package org.sadtech.bot.vcs.core.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.sadtech.bot.vcs.core.domain.entity.PullRequest;

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
    private static final Integer PR_COUNT = 4;

    public static Optional<String> pullRequestForReview(@NonNull List<PullRequest> pullRequestsReviews) {
        if (!pullRequestsReviews.isEmpty()) {
            return Optional.of(
                    pullRequestsReviews.stream()
                            .sorted(COMPARATOR)
                            .limit(PR_COUNT)
                            .map(MessageUtils::topPr)
                            .collect(Collectors.joining("\n"))
            );
        }
        return Optional.empty();
    }

    private static String topPr(PullRequest pullRequest) {
        return Smile.statusPr(pullRequest.getUpdateDate()) + " " +
                link(pullRequest.getTitle(), pullRequest.getUrl()) +
                Smile.BR;
    }

    @NonNull
    private static String link(String name, String url) {
        return "[" + name + "](" + url + ")";
    }

}
