package org.sadtech.bot.vcs.bitbucket.app.service.executor;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.sadtech.bot.vcs.bitbucket.sdk.domain.CommentJson;

@Getter
@RequiredArgsConstructor
public class ResultScan {

    private final String commentApiUrl;
    private final Long pullRequestId;
    private final CommentJson commentJson;

}
