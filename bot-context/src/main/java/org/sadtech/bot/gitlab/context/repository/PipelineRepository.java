package org.sadtech.bot.gitlab.context.repository;

import lombok.NonNull;
import org.sadtech.bot.gitlab.context.domain.PipelineStatus;
import org.sadtech.bot.gitlab.context.domain.entity.Pipeline;
import org.sadtech.haiti.context.page.Pagination;
import org.sadtech.haiti.context.page.Sheet;
import org.sadtech.haiti.context.repository.SimpleManagerRepository;

import java.util.Set;

/**
 * // TODO: 17.01.2021 Добавить описание.
 *
 * @author upagge 17.01.2021
 */
public interface PipelineRepository extends SimpleManagerRepository<Pipeline, Long> {

    Sheet<Pipeline> findAllByStatuses(@NonNull Set<PipelineStatus> statuses, @NonNull Pagination pagination);
}
