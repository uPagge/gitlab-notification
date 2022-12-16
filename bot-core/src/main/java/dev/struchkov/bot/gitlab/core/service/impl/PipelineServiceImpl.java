package dev.struchkov.bot.gitlab.core.service.impl;

import dev.struchkov.bot.gitlab.context.domain.ExistContainer;
import dev.struchkov.bot.gitlab.context.domain.PersonInformation;
import dev.struchkov.bot.gitlab.context.domain.PipelineStatus;
import dev.struchkov.bot.gitlab.context.domain.entity.Person;
import dev.struchkov.bot.gitlab.context.domain.entity.Pipeline;
import dev.struchkov.bot.gitlab.context.domain.filter.PipelineFilter;
import dev.struchkov.bot.gitlab.context.domain.notify.pipeline.PipelineNotify;
import dev.struchkov.bot.gitlab.context.repository.PipelineRepository;
import dev.struchkov.bot.gitlab.context.service.NotifyService;
import dev.struchkov.bot.gitlab.context.service.PipelineService;
import dev.struchkov.bot.gitlab.core.service.impl.filter.PipelineFilterService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static dev.struchkov.bot.gitlab.context.domain.PipelineStatus.CANCELED;
import static dev.struchkov.bot.gitlab.context.domain.PipelineStatus.FAILED;
import static dev.struchkov.bot.gitlab.context.domain.PipelineStatus.SKIPPED;
import static dev.struchkov.bot.gitlab.context.domain.PipelineStatus.SUCCESS;
import static dev.struchkov.haiti.context.exception.NotFoundException.notFoundException;
import static dev.struchkov.haiti.utils.Checker.checkNotNull;

/**
 * Реализация сервиса для работы с пайплайнами.
 *
 * @author upagge 17.01.2021
 */
@Service
@RequiredArgsConstructor
public class PipelineServiceImpl implements PipelineService {

    // Статусы пайплайнов, о которых нужно уведомить
    private static final Set<PipelineStatus> notificationStatus = Set.of(FAILED, SUCCESS, CANCELED, SKIPPED);

    private final NotifyService notifyService;
    private final PipelineRepository repository;
    private final PipelineFilterService pipelineFilterService;

    private final PersonInformation personInformation;

    @Override
    @Transactional
    public Pipeline create(@NonNull Pipeline pipeline) {
        final Pipeline newPipeline = repository.save(pipeline);
        notifyNewPipeline(pipeline, "n/a");
        return newPipeline;
    }

    @Override
    @Transactional
    public List<Pipeline> createAll(@NonNull List<Pipeline> newPipelines) {
        return newPipelines.stream()
                .map(this::create)
                .collect(Collectors.toList());
    }

    private void notifyNewPipeline(Pipeline pipeline, String oldStatus) {
        if (isNeedNotifyNewPipeline(pipeline)) {
            notifyService.send(
                    PipelineNotify.builder()
                            .newStatus(pipeline.getStatus().name())
                            .pipelineId(pipeline.getId())
                            .refName(pipeline.getRef())
                            .webUrl(pipeline.getWebUrl())
                            .oldStatus(oldStatus)
                            .build()
            );
        }
    }

    @Override
    public Pipeline update(@NonNull Pipeline pipeline) {
        final Pipeline oldPipeline = repository.findById(pipeline.getId())
                .orElseThrow(notFoundException("Pipeline не найден"));

        if (!oldPipeline.getUpdated().equals(pipeline.getUpdated())) {
            notifyNewPipeline(pipeline, oldPipeline.getStatus().name());
            return repository.save(pipeline);
        }

        return oldPipeline;
    }

    private boolean isNeedNotifyNewPipeline(@NonNull Pipeline pipeline) {
        final Person personPipelineCreator = pipeline.getPerson();
        return notificationStatus.contains(pipeline.getStatus()) // Пайплайн имеет статус необходимый для уведомления
                && checkNotNull(personPipelineCreator) // Создатель пайплайна не null
                && personInformation.getId().equals(personPipelineCreator.getId()); // Пользователь приложения является инициатором пайплайна
    }

    @Override
    public Page<Pipeline> getAllByStatuses(@NonNull Set<PipelineStatus> statuses, @NonNull Pageable pagination) {
        return repository.findAllByStatuses(statuses, pagination);
    }

    @Override
    public Page<Pipeline> getAll(@NonNull PipelineFilter filter, @NonNull Pageable pagination) {
        return pipelineFilterService.getAll(filter, pagination);
    }

    @Override
    public ExistContainer<Pipeline, Long> existsById(@NonNull Set<Long> pipelineIds) {
        final List<Pipeline> existsEntity = repository.findAllById(pipelineIds);
        final Set<Long> existsIds = existsEntity.stream().map(Pipeline::getId).collect(Collectors.toSet());
        if (existsIds.containsAll(pipelineIds)) {
            return ExistContainer.allFind(existsEntity);
        } else {
            final Set<Long> noExistsId = pipelineIds.stream()
                    .filter(id -> !existsIds.contains(id))
                    .collect(Collectors.toSet());
            return ExistContainer.notAllFind(existsEntity, noExistsId);
        }
    }

    @Override
    public void deleteAllById(Set<Long> pipelineIds) {
        repository.deleteAllByIds(pipelineIds);
    }

}
