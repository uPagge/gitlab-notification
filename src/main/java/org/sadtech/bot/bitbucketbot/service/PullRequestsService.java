package org.sadtech.bot.bitbucketbot.service;

import lombok.NonNull;
import org.sadtech.basic.context.service.SimpleManagerService;
import org.sadtech.basic.context.service.simple.FilterService;
import org.sadtech.bot.bitbucketbot.domain.IdAndStatusPr;
import org.sadtech.bot.bitbucketbot.domain.PullRequestStatus;
import org.sadtech.bot.bitbucketbot.domain.ReviewerStatus;
import org.sadtech.bot.bitbucketbot.domain.entity.PullRequest;
import org.sadtech.bot.bitbucketbot.domain.entity.PullRequestMini;
import org.sadtech.bot.bitbucketbot.domain.filter.PullRequestFilter;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface PullRequestsService extends SimpleManagerService<PullRequest, Long>, FilterService<PullRequest, PullRequestFilter> {

    @NonNull
    List<PullRequest> getAllByReviewerAndStatuses(@NonNull String login, @NonNull ReviewerStatus reviewerStatus, @NonNull Set<PullRequestStatus> pullRequestStatuses);

    List<PullRequest> getAllByAuthorAndReviewerStatus(@NonNull String login, @NonNull ReviewerStatus status);

    /**
     * Получить все идентификаторы вместе со статусами.
     *
     * @param statuses Статусы ПРов
     * @return Объект, содержащий идентификатор и статус ПР
     */
    Set<IdAndStatusPr> getAllId(Set<PullRequestStatus> statuses);

    Optional<PullRequestMini> getMiniInfo(@NonNull Long pullRequestId);

}
