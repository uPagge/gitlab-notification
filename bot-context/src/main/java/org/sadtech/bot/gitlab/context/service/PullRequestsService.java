package org.sadtech.bot.gitlab.context.service;

import lombok.NonNull;
import org.sadtech.bot.gitlab.context.domain.IdAndStatusPr;
import org.sadtech.bot.gitlab.context.domain.entity.PullRequest;
import org.sadtech.bot.gitlab.context.domain.entity.PullRequestMini;
import org.sadtech.bot.gitlab.context.domain.filter.PullRequestFilter;
import org.sadtech.bot.vsc.context.domain.PullRequestStatus;
import org.sadtech.bot.vsc.context.domain.ReviewerStatus;
import org.sadtech.haiti.context.service.SimpleManagerService;
import org.sadtech.haiti.filter.FilterService;

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

    /**
     * Получить все ПР с определенным статусом, который есть хотя бы у одного ревьювера.
     *
     * @param login  Автор ПР.
     * @param status Статус ревьювера.
     */
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
