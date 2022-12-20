package dev.struchkov.bot.gitlab.context.utils;

import dev.struchkov.bot.gitlab.context.domain.entity.MergeRequest;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;

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

    public static Optional<String> mergeRequestForReview(@NonNull List<MergeRequest> mergeRequestsReviews) {
        if (!mergeRequestsReviews.isEmpty()) {
            return Optional.of(
                    mergeRequestsReviews.stream()
                            .map(MessageUtils::generateStringItemMergeRequestReview)
                            .collect(Collectors.joining("\n"))
            );
        }
        return Optional.empty();
    }

    public static Optional<String> mergeRequestForNeedWork(@NonNull List<MergeRequest> mergeRequestNeedWork) {
        if (!mergeRequestNeedWork.isEmpty()) {
            return Optional.of(
                    mergeRequestNeedWork.stream()
                            .map(MessageUtils::generateStringItemMergeRequestNeedWork)
                            .collect(Collectors.joining("\n"))
            );
        }
        return Optional.empty();
    }

    private static String generateStringItemMergeRequestNeedWork(MergeRequest mergeRequest) {
        return "-- " + link(mergeRequest.getTitle(), mergeRequest.getWebUrl());
    }

    private static String generateStringItemMergeRequestReview(MergeRequest mergeRequest) {
        return Smile.statusPr(mergeRequest.getUpdatedDate()) + " " +
                link(mergeRequest.getTitle(), mergeRequest.getWebUrl());
    }

    private static String link(String name, String url) {
        return "[" + name + "](" + url + ")";
    }

}
