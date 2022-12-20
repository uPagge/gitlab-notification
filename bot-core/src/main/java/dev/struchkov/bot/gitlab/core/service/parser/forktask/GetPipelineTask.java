package dev.struchkov.bot.gitlab.core.service.parser.forktask;

import dev.struchkov.bot.gitlab.core.utils.StringUtils;
import dev.struchkov.bot.gitlab.sdk.domain.PipelineJson;
import dev.struchkov.haiti.utils.network.HttpParse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.text.MessageFormat;
import java.util.concurrent.RecursiveTask;

import static dev.struchkov.haiti.context.exception.ConvertException.convertException;
import static dev.struchkov.haiti.utils.network.HttpParse.ACCEPT;

@Slf4j
@RequiredArgsConstructor
public class GetPipelineTask extends RecursiveTask<PipelineJson> {

    private final String urlPipeline;
    private final long projectId;
    private final long pipelineId;
    private final String gitlabToken;

    @Override
    @SneakyThrows
    protected PipelineJson compute() {
        Thread.sleep(100);
        final PipelineJson pipelineJson = HttpParse.request(
                        MessageFormat.format(urlPipeline, projectId, pipelineId)
                )
                .header(ACCEPT)
                .header(StringUtils.H_PRIVATE_TOKEN, gitlabToken)
                .execute(PipelineJson.class)
                .orElseThrow(convertException("Ошибка обновления Pipelines"));
        pipelineJson.setProjectId(projectId);
        return pipelineJson;
    }

}
