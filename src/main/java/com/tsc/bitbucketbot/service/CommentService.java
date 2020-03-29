package com.tsc.bitbucketbot.service;

import com.tsc.bitbucketbot.domain.Pagination;
import com.tsc.bitbucketbot.domain.entity.Comment;
import lombok.NonNull;
import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.util.List;

public interface CommentService {

    Long getLastCommentId();

    Page<Comment> getAll(@NonNull Pagination pagination);

    @NonNull
    List<Comment> getAllBetweenDate(LocalDate dateFrom, LocalDate dateTo);

    void save(@NonNull Comment comment);

    void delete(@NonNull Long id);

}
