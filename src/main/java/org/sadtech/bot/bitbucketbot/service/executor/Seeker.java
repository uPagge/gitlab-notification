package org.sadtech.bot.bitbucketbot.service.executor;

import lombok.RequiredArgsConstructor;
import org.sadtech.bot.bitbucketbot.dto.bitbucket.CommentJson;
import org.sadtech.bot.bitbucketbot.service.Utils;

import java.util.Optional;
import java.util.concurrent.Callable;

@RequiredArgsConstructor
public class Seeker implements Callable<Optional<ResultScan>> {

    private final DataScan dataScan;
    private final String token;

    @Override
    public Optional<ResultScan> call() {
        return Utils.urlToJson(dataScan.getUrlComment(), token, CommentJson.class)
                .map(
                        commentJson -> new ResultScan(
                                dataScan.getUrlComment(),
                                dataScan.getPullRequestId(),
                                commentJson)
                );
    }

}
