package org.sadtech.bot.gitlab.app.service.executor;

import lombok.RequiredArgsConstructor;
import org.sadtech.bot.gitlab.sdk.domain.CommentJson;

import java.util.Optional;
import java.util.concurrent.Callable;

@RequiredArgsConstructor
public class Seeker implements Callable<Optional<CommentJson>> {

    private final DataScan dataScan;
    private final String token;

    @Override
    public Optional<CommentJson> call() {
        return Utils.urlToJson(dataScan.getUrlComment(), token, CommentJson.class)
                .map(
                        commentJson -> {
                            commentJson.setCustomPullRequestId(dataScan.getPullRequestId());
                            commentJson.setCustomCommentApiUrl(dataScan.getUrlComment());
                            return commentJson;
                        }
                );
    }

}
