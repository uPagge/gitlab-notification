package org.sadtech.bot.bitbucketbot.service;

import lombok.NonNull;
import org.sadtech.bot.bitbucketbot.domain.Pagination;
import org.sadtech.bot.bitbucketbot.domain.entity.Comment;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface CommentService {

    Long getLastCommentId();

    Page<Comment> getAll(@NonNull Pagination pagination);

    @NonNull
    List<Comment> getAllBetweenDate(LocalDateTime dateFrom, LocalDateTime dateTo);

    Comment create(@NonNull Comment comment);

    void delete(@NonNull Long id);

    Optional<Comment> getProxyById(@NonNull Long id);

    List<Comment> createAll(List<Comment> newComments);

    Comment update(Comment comment);

    List<Comment> getAllById(@NonNull Set<Long> ids);
}
