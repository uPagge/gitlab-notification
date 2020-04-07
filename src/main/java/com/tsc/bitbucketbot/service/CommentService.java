package com.tsc.bitbucketbot.service;

import com.tsc.bitbucketbot.domain.Pagination;
import com.tsc.bitbucketbot.domain.entity.Comment;
import lombok.NonNull;
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
