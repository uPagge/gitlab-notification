package org.sadtech.bot.bitbucketbot.service;

import lombok.NonNull;
import org.sadtech.basic.context.service.SimpleManagerService;
import org.sadtech.bot.bitbucketbot.domain.entity.Comment;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public interface CommentService extends SimpleManagerService<Comment, Long> {

    Long getLastCommentId();

    List<Comment> getAllBetweenDate(@NonNull LocalDateTime dateFrom, @NonNull LocalDateTime dateTo);

    List<Comment> getAllById(@NonNull Set<Long> ids);

}
