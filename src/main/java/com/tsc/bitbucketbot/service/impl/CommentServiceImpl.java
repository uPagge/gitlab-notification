package com.tsc.bitbucketbot.service.impl;

import com.tsc.bitbucketbot.domain.Pagination;
import com.tsc.bitbucketbot.domain.entity.Comment;
import com.tsc.bitbucketbot.repository.jpa.CommentRepository;
import com.tsc.bitbucketbot.service.CommentService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;

    @Override
    public Long getLastCommentId() {
        return commentRepository.findFirstByOrderByIdDesc().map(Comment::getId).orElse(0L);
    }

    @Override
    public Page<Comment> getAll(@NonNull Pagination pagination) {
        return commentRepository.findAll(PageRequest.of(pagination.getPage(), pagination.getSize()));
    }

    @Override
    public @NonNull List<Comment> getAllBetweenDate(LocalDate dateFrom, LocalDate dateTo) {
        return commentRepository.findByDateBetween(dateFrom, dateTo);
    }

    @Override
    public void save(@NonNull Comment comment) {
        commentRepository.save(comment);
    }

    @Override
    public void delete(@NonNull Long id) {
        commentRepository.deleteById(id);
    }

}
