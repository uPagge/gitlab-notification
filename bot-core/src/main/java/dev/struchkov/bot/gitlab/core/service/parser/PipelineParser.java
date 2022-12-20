package dev.struchkov.bot.gitlab.core.service.parser;

import dev.struchkov.bot.gitlab.context.domain.ExistContainer;
import dev.struchkov.bot.gitlab.context.domain.PipelineStatus;
import dev.struchkov.bot.gitlab.context.domain.entity.Pipeline;
import dev.struchkov.bot.gitlab.context.service.PipelineService;
import dev.struchkov.bot.gitlab.context.service.ProjectService;
import dev.struchkov.bot.gitlab.core.config.properties.GitlabProperty;
import dev.struchkov.bot.gitlab.core.config.properties.PersonProperty;
import dev.struchkov.bot.gitlab.core.service.parser.forktask.GetPipelineShortTask;
import dev.struchkov.bot.gitlab.core.service.parser.forktask.GetPipelineTask;
import dev.struchkov.bot.gitlab.core.utils.StringUtils;
import dev.struchkov.bot.gitlab.sdk.domain.PipelineJson;
import dev.struchkov.bot.gitlab.sdk.domain.PipelineShortJson;
import dev.struchkov.haiti.utils.network.HttpParse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.stream.Collectors;

import static dev.struchkov.bot.gitlab.context.domain.PipelineStatus.CREATED;
import static dev.struchkov.bot.gitlab.context.domain.PipelineStatus.MANUAL;
import static dev.struchkov.bot.gitlab.context.domain.PipelineStatus.PENDING;
import static dev.struchkov.bot.gitlab.context.domain.PipelineStatus.PREPARING;
import static dev.struchkov.bot.gitlab.context.domain.PipelineStatus.RUNNING;
import static dev.struchkov.bot.gitlab.context.domain.PipelineStatus.WAITING_FOR_RESOURCE;
import static dev.struchkov.haiti.utils.Checker.checkNotEmpty;
import static dev.struchkov.haiti.utils.concurrent.ForkJoinUtils.pullTaskResult;
import static dev.struchkov.haiti.utils.concurrent.ForkJoinUtils.pullTaskResults;
import static dev.struchkov.haiti.utils.network.HttpParse.ACCEPT;

/**
 * Парсер пайплайнов.
 *
 * @author upagge 17.01.2021
 */
@Slf4j
@Service
public class PipelineParser {

    public static final Integer COUNT = 100;
    private static final Set<PipelineStatus> oldStatus = Set.of(
            CREATED, WAITING_FOR_RESOURCE, PREPARING, PENDING, RUNNING, MANUAL
    );

    private final PipelineService pipelineService;
    private final ProjectService projectService;
    private final GitlabProperty gitlabProperty;
    private final PersonProperty personProperty;
    private final ConversionService conversionService;
    private final ForkJoinPool forkJoinPool;

    private LocalDateTime lastUpdate = LocalDateTime.now();

    public PipelineParser(
            PipelineService pipelineService,
            ProjectService projectService,
            GitlabProperty gitlabProperty,
            PersonProperty personProperty,
            ConversionService conversionService,
            @Qualifier("parserPool") ForkJoinPool forkJoinPool
    ) {
        this.pipelineService = pipelineService;
        this.projectService = projectService;
        this.gitlabProperty = gitlabProperty;
        this.personProperty = personProperty;
        this.conversionService = conversionService;
        this.forkJoinPool = forkJoinPool;
    }

    public void scanNewPipeline() {
        log.debug("Старт обработки новых пайплайнов");
        final Set<Long> projectIds = projectService.getAllIds();

        final Map<Long, Long> pipelineProjectMap = getPipelineShortJsons(projectIds).stream()
                .collect(Collectors.toMap(PipelineShortJson::getId, PipelineShortJson::getProjectId));

        if (!pipelineProjectMap.isEmpty()) {
            final ExistContainer<Pipeline, Long> existContainer = pipelineService.existsById(pipelineProjectMap.keySet());

            if (!existContainer.isAllFound()) {
                final Set<Long> idsNotFound = existContainer.getIdNoFound();

                final List<ForkJoinTask<PipelineJson>> tasks = idsNotFound.stream()
                        .map(pipelineId -> new GetPipelineTask(
                                gitlabProperty.getUrlPipeline(),
                                pipelineProjectMap.get(pipelineId),
                                pipelineId,
                                personProperty.getToken()
                        ))
                        .map(forkJoinPool::submit)
                        .collect(Collectors.toList());

                final List<PipelineJson> pipelineJsons = pullTaskResult(tasks);
                if (checkNotEmpty(pipelineJsons)) {
                    final List<Pipeline> newPipelines = pipelineJsons.stream()
                            .map(json -> conversionService.convert(json, Pipeline.class))
                            .collect(Collectors.toList());
                    pipelineService.createAll(newPipelines);
                }
            }

        }

        log.debug("Конец обработки новых пайплайнов");
    }

    private List<PipelineShortJson> getPipelineShortJsons(Set<Long> projectIds) {
        LocalDateTime newLastUpdate = LocalDateTime.now();
        final List<ForkJoinTask<List<PipelineShortJson>>> tasks = projectIds.stream()
                .map(projectId -> new GetPipelineShortTask(
                        gitlabProperty.getUrlPipelines(),
                        projectId,
                        lastUpdate,
                        personProperty.getToken()
                ))
                .map(forkJoinPool::submit)
                .collect(Collectors.toList());

        final List<PipelineShortJson> pipelineJsons = pullTaskResults(tasks);

        lastUpdate = newLastUpdate;
        return pipelineJsons;
    }


    public void scanOldPipeline() {
        log.debug("Старт обработки старых пайплайнов");
        final List<Pipeline> pipelines = pipelineService.getAllByStatuses(oldStatus);

        for (Pipeline pipeline : pipelines) {
            final Optional<Pipeline> optNewPipeline = HttpParse.request(
                            MessageFormat.format(gitlabProperty.getUrlPipeline(), pipeline.getProjectId(), pipeline.getId())
                    )
                    .header(ACCEPT)
                    .header(StringUtils.H_PRIVATE_TOKEN, personProperty.getToken())
                    .execute(PipelineJson.class)
                    .map(json -> conversionService.convert(json, Pipeline.class));
            if (optNewPipeline.isPresent()) {
                final Pipeline newPipeline = optNewPipeline.get();
                pipelineService.update(newPipeline);
            } else {
                log.error("Ошибка обновления пайплайна. ProjectId:{}, PipelineId:{}", pipeline.getProjectId(), pipeline.getId());
            }
        }

        log.debug("Конец обработки старых пайплайнов");
    }

}
