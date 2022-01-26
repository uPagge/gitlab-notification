package dev.struchkov.bot.gitlab.context.service;

import dev.struchkov.bot.gitlab.context.domain.PipelineStatus;
import dev.struchkov.bot.gitlab.context.domain.entity.Pipeline;
import dev.struchkov.bot.gitlab.context.domain.filter.PipelineFilter;
import dev.struchkov.haiti.context.page.Pagination;
import dev.struchkov.haiti.context.page.Sheet;
import dev.struchkov.haiti.context.service.SimpleManagerService;
import dev.struchkov.haiti.context.service.simple.FilterService;
import lombok.NonNull;

import java.util.Set;

/**
 * // TODO: 17.01.2021 Добавить описание.
 *
 * @author upagge 17.01.2021
 */
public interface PipelineService extends SimpleManagerService<Pipeline, Long>, FilterService<Pipeline, PipelineFilter> {

    Sheet<Pipeline> getAllByStatuses(@NonNull Set<PipelineStatus> statuses, @NonNull Pagination pagination);

}
