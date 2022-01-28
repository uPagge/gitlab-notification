package dev.struchkov.bot.gitlab.core.service.impl;

import dev.struchkov.bot.gitlab.context.domain.PersonInformation;
import dev.struchkov.bot.gitlab.context.domain.PipelineStatus;
import dev.struchkov.bot.gitlab.context.domain.entity.Person;
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
 * Реализация сервиса для работы с пайплайнами.
 *
 * @author upagge 17.01.2021
 */
@Service
public class PipelineServiceImpl extends AbstractSimpleManagerService<Pipeline, Long> implements PipelineService {

    // Статусы пайплайнов, о которых нужно уведомить
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
        notifyNewPipeline(pipeline, "n/a");
        return newPipeline;
    }

    private void notifyNewPipeline(Pipeline pipeline, String oldStatus) {
        if (isNeedNotifyNewPipeline(pipeline)) {
            notifyService.send(
                    PipelineNotify.builder()
                            .newStatus(pipeline.getStatus().name())
                            .pipelineId(pipeline.getId())
                            .projectName(pipeline.getProject().getName())
                            .refName(pipeline.getRef())
                            .webUrl(pipeline.getWebUrl())
                            .oldStatus(oldStatus)
                            .build()
            );
        }
    }

    @Override
    public Pipeline update(@NonNull Pipeline pipeline) {
        final Pipeline oldPipeline = pipelineRepository.findById(pipeline.getId())
                .orElseThrow(NotFoundException.supplier("Pipeline не найден"));

        if (!oldPipeline.getUpdated().equals(pipeline.getUpdated())) {
            pipeline.setProject(oldPipeline.getProject());
            notifyNewPipeline(pipeline, oldPipeline.getStatus().name());
            return pipelineRepository.save(pipeline);
        }

        return oldPipeline;
    }

    private boolean isNeedNotifyNewPipeline(@NonNull Pipeline pipeline) {
        final Person personPipelineCreator = pipeline.getPerson();
        return notificationStatus.contains(pipeline.getStatus()) // Пайплайн имеет статус необходимый для уведомления
                && personPipelineCreator != null
                && personInformation.getId().equals(personPipelineCreator.getId()); // Пользователь приложения является инициатором пайплайна
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
