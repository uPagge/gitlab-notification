package dev.struchkov.bot.gitlab.core.service.parser;

import dev.struchkov.bot.gitlab.context.domain.ExistContainer;
import dev.struchkov.bot.gitlab.context.domain.PipelineStatus;
import dev.struchkov.bot.gitlab.context.domain.entity.Pipeline;
import dev.struchkov.bot.gitlab.context.service.PipelineService;
import dev.struchkov.bot.gitlab.context.service.ProjectService;
import dev.struchkov.bot.gitlab.core.config.properties.GitlabProperty;
import dev.struchkov.bot.gitlab.core.config.properties.PersonProperty;
import dev.struchkov.bot.gitlab.core.utils.StringUtils;
import dev.struchkov.bot.gitlab.sdk.domain.PipelineJson;
import dev.struchkov.haiti.utils.network.HttpParse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static dev.struchkov.bot.gitlab.context.domain.PipelineStatus.CREATED;
import static dev.struchkov.bot.gitlab.context.domain.PipelineStatus.MANUAL;
import static dev.struchkov.bot.gitlab.context.domain.PipelineStatus.PENDING;
import static dev.struchkov.bot.gitlab.context.domain.PipelineStatus.PREPARING;
import static dev.struchkov.bot.gitlab.context.domain.PipelineStatus.RUNNING;
import static dev.struchkov.bot.gitlab.context.domain.PipelineStatus.WAITING_FOR_RESOURCE;
import static dev.struchkov.haiti.context.exception.ConvertException.convertException;
import static dev.struchkov.haiti.utils.Checker.checkNotEmpty;
import static dev.struchkov.haiti.utils.network.HttpParse.ACCEPT;

/**
 * Парсер пайплайнов.
 *
 * @author upagge 17.01.2021
 */
@Slf4j
@Service
@RequiredArgsConstructor
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

    private LocalDateTime lastUpdate = LocalDateTime.now();

    public void scanNewPipeline() {
        log.debug("Старт обработки новых папйплайнов");
        int page = 0;
        final Set<Long> projectIds = projectService.getAllIds();

        for (Long projectId : projectIds) {
            processingProject(projectId);
        }

        log.debug("Конец обработки новых папйплайнов");
    }

    private void processingProject(Long projectId) {
        int page = 1;
        LocalDateTime newLastUpdate = LocalDateTime.now();
        List<PipelineJson> pipelineJsons = getPipelineJsons(projectId, page, lastUpdate);

        while (checkNotEmpty(pipelineJsons)) {

            final Set<Long> jsonIds = pipelineJsons.stream()
                    .map(PipelineJson::getId)
                    .collect(Collectors.toSet());

            final ExistContainer<Pipeline, Long> existContainer = pipelineService.existsById(jsonIds);

            if (!existContainer.isAllFound()) {

                final Set<Long> idsNotFound = existContainer.getIdNoFound();

                for (Long newId : idsNotFound) {
                    final Pipeline newPipeline = HttpParse.request(
                                    MessageFormat.format(gitlabProperty.getUrlPipeline(), projectId, newId)
                            )
                            .header(ACCEPT)
                            .header(StringUtils.H_PRIVATE_TOKEN, personProperty.getToken())
                            .execute(PipelineJson.class)
                            .map(json -> {
                                final Pipeline pipeline = conversionService.convert(json, Pipeline.class);
                                pipeline.setProjectId(projectId);
                                return pipeline;
                            })
                            .orElseThrow(convertException("Ошибка обновления Pipelines"));
                    pipelineService.create(newPipeline);
                }

            }

            pipelineJsons = getPipelineJsons(projectId, ++page, lastUpdate);
        }

        lastUpdate = newLastUpdate;
    }

    private List<PipelineJson> getPipelineJsons(Long projectId, int page, LocalDateTime afterUpdate) {
        return HttpParse.request(MessageFormat.format(gitlabProperty.getUrlPipelines(), projectId, page))
                .header(ACCEPT)
                .header(StringUtils.H_PRIVATE_TOKEN, personProperty.getToken())
                .getParameter("updated_after", afterUpdate.minusHours(12L).toString())
                .executeList(PipelineJson.class);
    }

    public void scanOldPipeline() {
        log.debug("Старт обработки старых папйплайнов");
        int page = 0;
        Page<Pipeline> pipelineSheet = pipelineService.getAllByStatuses(oldStatus, PageRequest.of(page, COUNT));

        while (pipelineSheet.hasContent()) {
            final List<Pipeline> pipelines = pipelineSheet.getContent();

            for (Pipeline pipeline : pipelines) {
                final Pipeline newPipeline = HttpParse.request(
                                MessageFormat.format(gitlabProperty.getUrlPipeline(), pipeline.getProjectId(), pipeline.getId())
                        )
                        .header(ACCEPT)
                        .header(StringUtils.H_PRIVATE_TOKEN, personProperty.getToken())
                        .execute(PipelineJson.class)
                        .map(json -> conversionService.convert(json, Pipeline.class))
                        .orElseThrow(convertException("Ошибка обновления Pipelines"));

                pipelineService.update(newPipeline);
            }

            pipelineSheet = pipelineService.getAllByStatuses(oldStatus, PageRequest.of(++page, COUNT));
        }
        log.debug("Конец обработки старых папйплайнов");
    }

}
