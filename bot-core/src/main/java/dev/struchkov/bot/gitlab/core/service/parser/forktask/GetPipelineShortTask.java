package dev.struchkov.bot.gitlab.core.service.parser.forktask;

import dev.struchkov.bot.gitlab.core.utils.StringUtils;
import dev.struchkov.bot.gitlab.sdk.domain.PipelineShortJson;
import dev.struchkov.haiti.utils.network.HttpParse;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.RecursiveTask;

import static dev.struchkov.haiti.utils.network.HttpParse.ACCEPT;

@Slf4j
@AllArgsConstructor
@RequiredArgsConstructor
public class GetPipelineShortTask extends RecursiveTask<List<PipelineShortJson>> {

    private static final int PAGE_COUNT = 100;

    private final String urlPipelines;
    private final long projectId;
    private int pageNumber = 0;
    private final LocalDateTime lastUpdate;
    private final String gitlabToken;

    @Override
    protected List<PipelineShortJson> compute() {
        final List<PipelineShortJson> jsons = getPipelineJsons();
        if (jsons.size() == PAGE_COUNT) {
            final GetPipelineShortTask newTask = new GetPipelineShortTask(urlPipelines, projectId, pageNumber + 1, lastUpdate, gitlabToken);
            newTask.fork();
            jsons.addAll(newTask.join());
        }
        jsons.forEach(pipelineJson -> pipelineJson.setProjectId(projectId));
        return jsons;
    }

    private List<PipelineShortJson> getPipelineJsons() {
        final List<PipelineShortJson> jsons = HttpParse.request(MessageFormat.format(urlPipelines, projectId, pageNumber, PAGE_COUNT))
                .header(ACCEPT)
                .header(StringUtils.H_PRIVATE_TOKEN, gitlabToken)
                .getParameter("updated_after", lastUpdate.minusHours(12L).toString())
                .executeList(PipelineShortJson.class);
        log.trace("Получено {} шт потенциально новых пайплайнов для проекта id:'{}' ", jsons.size(), projectId);
        return jsons;
    }
}
