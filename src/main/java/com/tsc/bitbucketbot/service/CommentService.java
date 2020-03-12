package com.tsc.bitbucketbot.service;

import lombok.NonNull;

public interface CommentService {

    Long getLastCommentId();

    void saveLastCommentId(@NonNull Long commentId);

}
