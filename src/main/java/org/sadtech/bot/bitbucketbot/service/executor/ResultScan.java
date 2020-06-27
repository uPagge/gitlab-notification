package org.sadtech.bot.bitbucketbot.service.executor;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.sadtech.bot.bitbucketbot.dto.bitbucket.CommentJson;

@Getter
@RequiredArgsConstructor
public class ResultScan {

    private final String urlComment;
    private final Long pullRequestId;
    private final CommentJson commentJson;

}
