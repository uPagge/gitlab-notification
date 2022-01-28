package dev.struchkov.bot.gitlab.core.service.impl;

import dev.struchkov.bot.gitlab.context.domain.PersonInformation;
import dev.struchkov.bot.gitlab.context.domain.PipelineStatus;
import dev.struchkov.bot.gitlab.context.domain.entity.Pipeline;
import dev.struchkov.bot.gitlab.context.domain.filter.PipelineFilter;
import dev.struchkov.bot.gitlab.context.domain.notify.pipeline.PipelineNotify;
import dev.struchkov.bot.gitlab.context.repository.PipelineRepository;
import dev.struchkov.bot.gitlab.context.service.NotifyService;
import dev.struchkov.bot.gitlab.context.service.PersonService;
import dev.struchkov.bot.gitlab.context.service.PipelineService;
import dev.struchkov.bot.gitlab.core.service.impl.filter.PipelineFilterService;
import dev.struchkov.haiti.context.exception.NotFoundException;
import dev.struchkov.haiti.context.page.Pagination;
import dev.struchkov.haiti.context.page.Sheet;
import dev.struchkov.haiti.core.service.AbstractSimpleManagerService;
import lombok.NonNull;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

import static dev.struchkov.bot.gitlab.context.domain.PipelineStatus.CANCELED;
import static dev.struchkov.bot.gitlab.context.domain.PipelineStatus.FAILED;
import static dev.struchkov.bot.gitlab.context.domain.PipelineStatus.SKIPPED;
import static dev.struchkov.bot.gitlab.context.domain.PipelineStatus.SUCCESS;

/**
 * // TODO: 17.01.2021 Добавить описание.
 *
 * @author upagge 17.01.2021
 */
@Service
public class PipelineServiceImpl extends AbstractSimpleManagerService<Pipeline, Long> implements PipelineService {

    private static final Set<PipelineStatus> notificationStatus = Set.of(FAILED, SUCCESS, CANCELED, SKIPPED);

    private final NotifyService notifyService;
    private final PipelineRepository pipelineRepository;
    private final PersonService personService;
    private final PipelineFilterService pipelineFilterService;

    private final PersonInformation personInformation;

    public PipelineServiceImpl(
            NotifyService notifyService,
            PipelineRepository pipelineRepository,
            PersonService personService,
            PipelineFilterService pipelineFilterService,
            PersonInformation personInformation
    ) {
        super(pipelineRepository);
        this.notifyService = notifyService;
        this.pipelineRepository = pipelineRepository;
        this.personService = personService;
        this.pipelineFilterService = pipelineFilterService;
        this.personInformation = personInformation;
    }

    @Override
    public Pipeline create(@NonNull Pipeline pipeline) {
        personService.create(pipeline.getPerson());
        final Pipeline newPipeline = pipelineRepository.save(pipeline);

        if (
                notificationStatus.contains(pipeline.getStatus())
                        && pipeline.getPerson() != null
                        && personInformation.getId().equals(pipeline.getPerson().getId())
        ) {
            notifyService.send(
                    PipelineNotify.builder()
                            .newStatus(pipeline.getStatus().name())
                            .pipelineId(pipeline.getId())
                            .projectName(pipeline.getProject().getName())
                            .refName(pipeline.getRef())
                            .webUrl(pipeline.getWebUrl())
                            .build()
            );
        }

        return newPipeline;
    }

    @Override
    public Pipeline update(@NonNull Pipeline pipeline) {
        final Pipeline oldPipeline = pipelineRepository.findById(pipeline.getId())
                .orElseThrow(NotFoundException.supplier("Pipeline не найден"));

        if (!oldPipeline.getUpdated().equals(pipeline.getUpdated())) {
            pipeline.setProject(oldPipeline.getProject());

            if (
                    notificationStatus.contains(pipeline.getStatus())
                            && pipeline.getPerson() != null
                            && personInformation.getId().equals(pipeline.getPerson().getId())
            ) {
                notifyService.send(
                        PipelineNotify.builder()
                                .pipelineId(pipeline.getId())
                                .webUrl(pipeline.getWebUrl())
                                .projectName(pipeline.getProject().getName())
                                .refName(pipeline.getRef())
                                .newStatus(pipeline.getStatus().name())
                                .oldStatus(oldPipeline.getStatus().name())
                                .build()
                );

                return pipelineRepository.save(pipeline);
            }

        }

        return oldPipeline;
    }

    @Override
    public Sheet<Pipeline> getAllByStatuses(@NonNull Set<PipelineStatus> statuses, @NonNull Pagination pagination) {
        return pipelineRepository.findAllByStatuses(statuses, pagination);
    }

    @Override
    public Sheet<Pipeline> getAll(@NonNull PipelineFilter filter, @NonNull Pagination pagination) {
        return pipelineFilterService.getAll(filter, pagination);
    }

    @Override
    public Optional<Pipeline> getFirst(@NonNull PipelineFilter filter) {
        return pipelineFilterService.getFirst(filter);
    }

    @Override
    public boolean exists(@NonNull PipelineFilter filter) {
        return pipelineFilterService.exists(filter);
    }

    @Override
    public long count(@NonNull PipelineFilter filter) {
        return pipelineFilterService.count(filter);
    }

}
