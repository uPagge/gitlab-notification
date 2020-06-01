package org.sadtech.bot.bitbucketbot.service.executor;

import lombok.Data;
import org.sadtech.bot.bitbucketbot.dto.bitbucket.CommentJson;

@Data
public class ResultScan {

    private final String urlComment;
    private final String urlPr;
    private final CommentJson commentJson;

}
