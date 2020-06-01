package org.sadtech.bot.bitbucketbot.service;

import lombok.NonNull;
import org.sadtech.bot.bitbucketbot.domain.Pagination;
import org.sadtech.bot.bitbucketbot.domain.entity.Comment;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;

public interface CommentService {

    Long getLastCommentId();

    Page<Comment> getAll(@NonNull Pagination pagination);

    @NonNull
    List<Comment> getAllBetweenDate(LocalDateTime dateFrom, LocalDateTime dateTo);

    void save(@NonNull Comment comment);

    void delete(@NonNull Long id);

}
