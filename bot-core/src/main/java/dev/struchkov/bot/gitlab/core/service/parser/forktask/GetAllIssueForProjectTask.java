package dev.struchkov.bot.gitlab.core.service.parser.forktask;

import dev.struchkov.bot.gitlab.core.utils.StringUtils;
import dev.struchkov.bot.gitlab.sdk.domain.IssueJson;
import dev.struchkov.haiti.utils.network.HttpParse;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.text.MessageFormat;
import java.util.List;
import java.util.concurrent.RecursiveTask;

import static dev.struchkov.haiti.utils.Checker.checkNotEmpty;
import static dev.struchkov.haiti.utils.network.HttpParse.ACCEPT;

/**
 * @author Dmitry Sheyko [24.01.2023]
 */
@Slf4j
@AllArgsConstructor
@RequiredArgsConstructor
public class GetAllIssueForProjectTask extends RecursiveTask<List<IssueJson>> {

    private static final int PAGE_COUNT = 100;

    private final long projectId;
    private int pageNumber = 1;
    private final String urlIssueOpen;
    private final String gitlabToken;

    @Override
    @SneakyThrows
    protected List<IssueJson> compute() {
        Thread.sleep(200);
        final List<IssueJson> issueJson = getIssueJsons();
        if (checkNotEmpty(issueJson) && issueJson.size() == PAGE_COUNT) {
            final GetAllIssueForProjectTask newTask = new GetAllIssueForProjectTask(projectId, pageNumber + 1, urlIssueOpen, gitlabToken);
            newTask.fork();
            issueJson.addAll(newTask.join());
        }
        return issueJson;
    }

    private List<IssueJson> getIssueJsons() {
        final List<IssueJson> jsons = HttpParse.request(MessageFormat.format(urlIssueOpen, projectId, pageNumber, PAGE_COUNT))
                .header(StringUtils.H_PRIVATE_TOKEN, gitlabToken)
                .header(ACCEPT)
                .executeList(IssueJson.class);
        log.trace("Получено {} шт потенциально новых Issue для проекта id:'{}' ", jsons.size(), projectId);
        return jsons;
    }

}