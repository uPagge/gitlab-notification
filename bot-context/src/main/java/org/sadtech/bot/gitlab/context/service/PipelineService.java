package org.sadtech.bot.gitlab.context.service;

import lombok.NonNull;
import org.sadtech.bot.gitlab.context.domain.PipelineStatus;
import org.sadtech.bot.gitlab.context.domain.entity.Pipeline;
import org.sadtech.bot.gitlab.context.domain.filter.PipelineFilter;
import org.sadtech.haiti.context.page.Pagination;
import org.sadtech.haiti.context.page.Sheet;
import org.sadtech.haiti.context.service.SimpleManagerService;
import org.sadtech.haiti.filter.FilterService;

import java.util.Set;

/**
 * // TODO: 17.01.2021 Добавить описание.
 *
 * @author upagge 17.01.2021
 */
public interface PipelineService extends SimpleManagerService<Pipeline, Long>, FilterService<Pipeline, PipelineFilter> {

    Sheet<Pipeline> getAllByStatuses(@NonNull Set<PipelineStatus> statuses, @NonNull Pagination pagination);

}
