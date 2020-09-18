package org.sadtech.bot.vcs.core.service;

import lombok.NonNull;
import org.sadtech.basic.context.service.SimpleManagerService;
import org.sadtech.basic.context.service.simple.FilterService;
import org.sadtech.bot.vcs.core.domain.IdAndStatusPr;
import org.sadtech.bot.vcs.core.domain.PullRequestStatus;
import org.sadtech.bot.vcs.core.domain.ReviewerStatus;
import org.sadtech.bot.vcs.core.domain.entity.PullRequest;
import org.sadtech.bot.vcs.core.domain.entity.PullRequestMini;
import org.sadtech.bot.vcs.core.domain.filter.PullRequestFilter;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface PullRequestsService extends SimpleManagerService<PullRequest, Long>, FilterService<PullRequest, PullRequestFilter> {

    /**
     * Получить все пулреквесты ревьювера с определенным статусом.
     *
     * @param login               Логин ревьювера
     * @param reviewerStatus      Статус ревьювера в ПР
     * @param pullRequestStatuses Статус ПР
     */
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
