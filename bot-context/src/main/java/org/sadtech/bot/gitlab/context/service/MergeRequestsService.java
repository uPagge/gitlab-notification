package org.sadtech.bot.gitlab.context.service;

import lombok.NonNull;
import org.sadtech.bot.gitlab.context.domain.IdAndStatusPr;
import org.sadtech.bot.gitlab.context.domain.MergeRequestState;
import org.sadtech.bot.gitlab.context.domain.entity.MergeRequest;
import org.sadtech.bot.gitlab.context.domain.entity.MergeRequestMini;
import org.sadtech.bot.gitlab.context.domain.filter.PullRequestFilter;
import org.sadtech.haiti.context.service.SimpleManagerService;
import org.sadtech.haiti.filter.FilterService;

import java.util.Optional;
import java.util.Set;

public interface MergeRequestsService extends SimpleManagerService<MergeRequest, Long>, FilterService<MergeRequest, PullRequestFilter> {

    /**
     * Получить все идентификаторы вместе со статусами.
     *
     * @param statuses Статусы ПРов
     * @return Объект, содержащий идентификатор и статус ПР
     */
    Set<IdAndStatusPr> getAllId(Set<MergeRequestState> statuses);

    Optional<MergeRequestMini> getMiniInfo(@NonNull Long pullRequestId);

}
