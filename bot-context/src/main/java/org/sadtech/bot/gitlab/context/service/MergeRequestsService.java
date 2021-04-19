package org.sadtech.bot.gitlab.context.service;

import org.sadtech.bot.gitlab.context.domain.IdAndStatusPr;
import org.sadtech.bot.gitlab.context.domain.MergeRequestState;
import org.sadtech.bot.gitlab.context.domain.entity.MergeRequest;
import org.sadtech.bot.gitlab.context.domain.filter.MergeRequestFilter;
import org.sadtech.haiti.context.service.SimpleManagerService;
import org.sadtech.haiti.context.service.simple.FilterService;

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
