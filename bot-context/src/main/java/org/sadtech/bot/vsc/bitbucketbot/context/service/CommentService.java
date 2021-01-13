package org.sadtech.bot.vsc.bitbucketbot.context.service;

import lombok.NonNull;
import org.sadtech.bot.vsc.bitbucketbot.context.domain.entity.Comment;
import org.sadtech.bot.vsc.bitbucketbot.context.domain.entity.Task;
import org.sadtech.haiti.context.service.SimpleManagerService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public interface CommentService extends SimpleManagerService<Comment, Long> {

    Long getLastCommentId();

    List<Comment> getAllBetweenDate(@NonNull LocalDateTime dateFrom, @NonNull LocalDateTime dateTo);

    List<Comment> getAllById(@NonNull Set<Long> ids);

    Comment convert(@NonNull Task task);

    Set<Long> existsById(@NonNull Set<Long> ids);

}
