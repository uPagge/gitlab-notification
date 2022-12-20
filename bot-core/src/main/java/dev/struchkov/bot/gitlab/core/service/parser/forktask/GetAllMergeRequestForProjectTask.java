package dev.struchkov.bot.gitlab.core.service.parser.forktask;

import dev.struchkov.bot.gitlab.core.utils.StringUtils;
import dev.struchkov.bot.gitlab.sdk.domain.MergeRequestJson;
import dev.struchkov.haiti.utils.network.HttpParse;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.text.MessageFormat;
import java.util.List;
import java.util.concurrent.RecursiveTask;

import static dev.struchkov.haiti.utils.network.HttpParse.ACCEPT;

@Slf4j
@AllArgsConstructor
@RequiredArgsConstructor
public class GetAllMergeRequestForProjectTask extends RecursiveTask<List<MergeRequestJson>> {

    private static final int PAGE_COUNT = 100;

    private final long projectId;
    private int pageNumber = 0;
    private final String urlMrOpen;
    private final String gitlabToken;

    @Override
    @SneakyThrows
    protected List<MergeRequestJson> compute() {
        Thread.sleep(100);
        final List<MergeRequestJson> mergeRequestJsons = getMergeRequestJsons();
        if (mergeRequestJsons.size() == PAGE_COUNT) {
            final GetAllMergeRequestForProjectTask newTask = new GetAllMergeRequestForProjectTask(projectId, pageNumber + 1, urlMrOpen, gitlabToken);
            newTask.fork();
            mergeRequestJsons.addAll(newTask.join());
        }
        return mergeRequestJsons;
    }

    private List<MergeRequestJson> getMergeRequestJsons() {
        final List<MergeRequestJson> jsons = HttpParse.request(MessageFormat.format(urlMrOpen, projectId, pageNumber, PAGE_COUNT))
                .header(StringUtils.H_PRIVATE_TOKEN, gitlabToken)
                .header(ACCEPT)
                .executeList(MergeRequestJson.class);
        log.trace("Получено {} шт потенциально новых MR для проекта id:'{}' ", jsons.size(), projectId);
        return jsons;
    }

}
