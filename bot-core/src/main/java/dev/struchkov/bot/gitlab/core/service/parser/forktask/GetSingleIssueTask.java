package dev.struchkov.bot.gitlab.core.service.parser.forktask;

import dev.struchkov.bot.gitlab.core.utils.StringUtils;
import dev.struchkov.bot.gitlab.sdk.domain.IssueJson;
import dev.struchkov.haiti.utils.network.HttpParse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.text.MessageFormat;
import java.util.Optional;
import java.util.concurrent.RecursiveTask;

import static dev.struchkov.haiti.utils.network.HttpParse.ACCEPT;

@Slf4j
@RequiredArgsConstructor
public class GetSingleIssueTask extends RecursiveTask<Optional<IssueJson>> {

    private final String urlIssue;
    private final long projectId;
    private final long issueTwoId;
    private final String gitlabToken;

    @Override
    @SneakyThrows
    protected Optional<IssueJson> compute() {
        Thread.sleep(200);
        final String mrUrl = MessageFormat.format(urlIssue, projectId, issueTwoId);
        return HttpParse.request(mrUrl)
                .header(ACCEPT)
                .header(StringUtils.H_PRIVATE_TOKEN, gitlabToken)
                .execute(IssueJson.class);
    }

}