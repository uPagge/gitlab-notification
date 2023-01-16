package dev.struchkov.bot.gitlab.core.service.parser.forktask;

import dev.struchkov.bot.gitlab.core.utils.HttpParse;
import dev.struchkov.bot.gitlab.sdk.domain.DiscussionJson;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import java.text.MessageFormat;
import java.util.List;
import java.util.concurrent.RecursiveTask;

import static dev.struchkov.bot.gitlab.core.utils.HttpParse.ACCEPT;
import static dev.struchkov.bot.gitlab.core.utils.StringUtils.H_PRIVATE_TOKEN;
import static dev.struchkov.haiti.utils.Checker.checkNotEmpty;

@AllArgsConstructor
@RequiredArgsConstructor
public class GetAllDiscussionForMergeRequestTask extends RecursiveTask<List<DiscussionJson>> {

    private static final int PAGE_COUNT = 100;

    private final String discussionsUrl;
    private final long projectId;
    private final long mergeRequestTwoId;
    private final String personalGitlabToken;
    private int page = 1;

    @Override
    @SneakyThrows
    protected List<DiscussionJson> compute() {
        Thread.sleep(100);
        final List<DiscussionJson> jsons = getDiscussionJson();
        if (checkNotEmpty(jsons) && jsons.size() == PAGE_COUNT) {
            final var newTask = new GetAllDiscussionForMergeRequestTask(discussionsUrl, projectId, mergeRequestTwoId, personalGitlabToken, page + 1);
            newTask.fork();
            jsons.addAll(newTask.join());
        }
        return jsons;
    }

    private List<DiscussionJson> getDiscussionJson() {
        return HttpParse.request(MessageFormat.format(discussionsUrl, projectId, mergeRequestTwoId, page, PAGE_COUNT))
                .header(ACCEPT)
                .header(H_PRIVATE_TOKEN, personalGitlabToken)
                .executeList(DiscussionJson.class);
    }

}
