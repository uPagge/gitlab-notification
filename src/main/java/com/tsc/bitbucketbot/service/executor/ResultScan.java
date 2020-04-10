package com.tsc.bitbucketbot.service.executor;

import com.tsc.bitbucketbot.dto.bitbucket.CommentJson;
import lombok.Data;

@Data
public class ResultScan {

    private final String urlComment;
    private final String urlPr;
    private final CommentJson commentJson;

}
