package dev.struchkov.bot.gitlab.core.service.parser.forktask;

import dev.struchkov.bot.gitlab.core.utils.StringUtils;
import dev.struchkov.bot.gitlab.sdk.domain.MergeRequestJson;
import dev.struchkov.haiti.utils.network.HttpParse;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.text.MessageFormat;
import java.util.List;
import java.util.concurrent.RecursiveTask;

import static dev.struchkov.haiti.utils.network.HttpParse.ACCEPT;

@Slf4j
@AllArgsConstructor
@RequiredArgsConstructor
public class GetMergeRequestFromGitlab extends RecursiveTask<List<MergeRequestJson>> {

    private static final int PAGE_COUNT = 100;

    private final long projectId;
    private int pageNumber = 0;
    private final String urlMrOpen;
    private final String personToken;

    @Override
    protected List<MergeRequestJson> compute() {
        final List<MergeRequestJson> mergeRequestJsons = getMergeRequestJsons(urlMrOpen, projectId, pageNumber, personToken);
        if (mergeRequestJsons.size() == PAGE_COUNT) {
            final GetMergeRequestFromGitlab newTask = new GetMergeRequestFromGitlab(projectId, pageNumber + 1, urlMrOpen, personToken);
            newTask.fork();
            final List<MergeRequestJson> result = newTask.join();
            mergeRequestJsons.addAll(result);
        }
        return mergeRequestJsons;
    }

    private List<MergeRequestJson> getMergeRequestJsons(String url, Long projectId, int page, String personToken) {
        final List<MergeRequestJson> jsons = HttpParse.request(MessageFormat.format(url, projectId, page, PAGE_COUNT))
                .header(StringUtils.H_PRIVATE_TOKEN, personToken)
                .header(ACCEPT)
                .executeList(MergeRequestJson.class);
        log.trace("Получено {} шт потенциально новых MR для проекта id:'{}' ", jsons.size(), projectId);
        return jsons;
    }

}
