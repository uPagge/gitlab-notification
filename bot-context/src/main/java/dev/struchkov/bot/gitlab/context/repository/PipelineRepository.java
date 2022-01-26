package dev.struchkov.bot.gitlab.context.repository;

import dev.struchkov.bot.gitlab.context.domain.entity.Pipeline;
import dev.struchkov.haiti.context.page.Pagination;
import dev.struchkov.haiti.context.page.Sheet;
import dev.struchkov.haiti.context.repository.SimpleManagerRepository;
import dev.struchkov.haiti.filter.FilterOperation;
import lombok.NonNull;
import dev.struchkov.bot.gitlab.context.domain.PipelineStatus;

import java.util.Set;

/**
 * // TODO: 17.01.2021 Добавить описание.
 *
 * @author upagge 17.01.2021
 */
public interface PipelineRepository extends SimpleManagerRepository<Pipeline, Long>, FilterOperation<Pipeline> {

    Sheet<Pipeline> findAllByStatuses(@NonNull Set<PipelineStatus> statuses, @NonNull Pagination pagination);
}
