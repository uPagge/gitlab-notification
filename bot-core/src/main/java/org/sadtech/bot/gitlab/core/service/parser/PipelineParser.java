package org.sadtech.bot.gitlab.core.service.parser;

import lombok.RequiredArgsConstructor;
import org.sadtech.bot.gitlab.context.domain.PipelineStatus;
import org.sadtech.bot.gitlab.context.domain.entity.Pipeline;
import org.sadtech.bot.gitlab.context.domain.entity.Project;
import org.sadtech.bot.gitlab.context.service.PipelineService;
import org.sadtech.bot.gitlab.context.service.ProjectService;
import org.sadtech.bot.gitlab.core.config.properties.GitlabProperty;
import org.sadtech.bot.gitlab.core.config.properties.PersonProperty;
import org.sadtech.bot.gitlab.sdk.domain.PipelineJson;
import org.sadtech.haiti.context.domain.ExistsContainer;
import org.sadtech.haiti.context.exception.ConvertException;
import org.sadtech.haiti.context.page.Sheet;
import org.sadtech.haiti.core.page.PaginationImpl;
import org.sadtech.haiti.utils.network.HttpParse;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.sadtech.bot.gitlab.context.domain.PipelineStatus.CREATED;
import static org.sadtech.bot.gitlab.context.domain.PipelineStatus.MANUAL;
import static org.sadtech.bot.gitlab.context.domain.PipelineStatus.PENDING;
import static org.sadtech.bot.gitlab.context.domain.PipelineStatus.PREPARING;
import static org.sadtech.bot.gitlab.context.domain.PipelineStatus.RUNNING;
import static org.sadtech.bot.gitlab.context.domain.PipelineStatus.WAITING_FOR_RESOURCE;
import static org.sadtech.haiti.utils.network.HttpParse.ACCEPT;
import static org.sadtech.haiti.utils.network.HttpParse.AUTHORIZATION;
import static org.sadtech.haiti.utils.network.HttpParse.BEARER;

/**
 * // TODO: 17.01.2021 Добавить описание.
 *
 * @author upagge 17.01.2021
 */

@Service
@RequiredArgsConstructor
public class PipelineParser {

    public static final Integer COUNT = 100;
    private static final Set<PipelineStatus> oldStatus = Stream.of(
            CREATED, WAITING_FOR_RESOURCE, PREPARING, PENDING, RUNNING, MANUAL
    ).collect(Collectors.toSet());
    private final PipelineService pipelineService;
    private final ProjectService projectService;
    private final GitlabProperty gitlabProperty;
    private final PersonProperty personProperty;
    private final ConversionService conversionService;

    private LocalDateTime lastUpdate = LocalDateTime.now();

    public void scanNewPipeline() {
        int page = 0;
        Sheet<Project> projectSheet = projectService.getAll(PaginationImpl.of(page, COUNT));

        while (projectSheet.hasContent()) {
            final List<Project> projects = projectSheet.getContent();

            for (Project project : projects) {
                processingProject(project);
            }

            projectSheet = projectService.getAll(PaginationImpl.of(++page, COUNT));
        }

    }

    private void processingProject(Project project) {
        int page = 1;
        LocalDateTime newLastUpdate = LocalDateTime.now();
        List<PipelineJson> pipelineJsons = getPipelineJsons(project.getId(), page, lastUpdate);

        while (!pipelineJsons.isEmpty()) {

            final Set<Long> jsonIds = pipelineJsons.stream()
                    .map(PipelineJson::getId)
                    .collect(Collectors.toSet());

            final ExistsContainer<Pipeline, Long> existsContainer = pipelineService.existsById(jsonIds);

            if (!existsContainer.isAllFound()) {

                final Collection<Long> idsNotFound = existsContainer.getIdNoFound();

                for (Long newId : idsNotFound) {
                    final Pipeline newPipeline = HttpParse.request(
                            MessageFormat.format(gitlabProperty.getUrlPipeline(), project.getId(), newId)
                    )
                            .header(ACCEPT)
                            .header(AUTHORIZATION, BEARER + personProperty.getToken())
                            .execute(PipelineJson.class)
                            .map(json -> {
                                final Pipeline pipeline = conversionService.convert(json, Pipeline.class);
                                pipeline.setProject(project);
                                return pipeline;
                            })
                            .orElseThrow(() -> new ConvertException("Ошибка обновления Pipelines"));
                    pipelineService.create(newPipeline);
                }

            }

            pipelineJsons = getPipelineJsons(project.getId(), ++page, lastUpdate);
        }

        lastUpdate = newLastUpdate;
    }

    private List<PipelineJson> getPipelineJsons(Long projectId, int page, LocalDateTime afterUpdate) {
        return HttpParse.request(MessageFormat.format(gitlabProperty.getUrlPipelines(), projectId, page))
                .header(ACCEPT)
                .header(HttpParse.AUTHORIZATION, HttpParse.BEARER + personProperty.getToken())
                .getParameter("updated_after", afterUpdate.minusHours(12L).toString())
                .executeList(PipelineJson.class);
    }

    public void scanOldPipeline() {
        int page = 0;
        Sheet<Pipeline> pipelineSheet = pipelineService.getAllByStatuses(oldStatus, PaginationImpl.of(page, COUNT));

        while (pipelineSheet.hasContent()) {
            final List<Pipeline> pipelines = pipelineSheet.getContent();

            for (Pipeline pipeline : pipelines) {
                final Pipeline newPipeline = HttpParse.request(
                        MessageFormat.format(gitlabProperty.getUrlPipeline(), pipeline.getProject().getId(), pipeline.getId())
                )
                        .header(ACCEPT)
                        .header(AUTHORIZATION, BEARER + personProperty.getToken())
                        .execute(PipelineJson.class)
                        .map(json -> conversionService.convert(json, Pipeline.class))
                        .orElseThrow(() -> new ConvertException("Ошибка обновления Pipelines"));

                pipelineService.update(newPipeline);
            }

            pipelineSheet = pipelineService.getAllByStatuses(oldStatus, PaginationImpl.of(++page, COUNT));
        }
    }

}
