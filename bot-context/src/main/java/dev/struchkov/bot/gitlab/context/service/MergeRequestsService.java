package dev.struchkov.bot.gitlab.context.service;

import dev.struchkov.bot.gitlab.context.domain.IdAndStatusPr;
import dev.struchkov.bot.gitlab.context.domain.MergeRequestState;
import dev.struchkov.bot.gitlab.context.domain.entity.MergeRequest;
import dev.struchkov.haiti.context.service.SimpleManagerService;
import dev.struchkov.haiti.context.service.simple.FilterService;
import dev.struchkov.bot.gitlab.context.domain.filter.MergeRequestFilter;

import java.util.Set;

public interface MergeRequestsService extends SimpleManagerService<MergeRequest, Long>, FilterService<MergeRequest, MergeRequestFilter> {

    /**
     * Получить все идентификаторы вместе со статусами.
     *
     * @param statuses Статусы ПРов
     * @return Объект, содержащий идентификатор и статус ПР
     */
    Set<IdAndStatusPr> getAllId(Set<MergeRequestState> statuses);

}
