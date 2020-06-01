package org.sadtech.bot.bitbucketbot.service.impl;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.sadtech.bot.bitbucketbot.config.InitConfig;
import org.sadtech.bot.bitbucketbot.domain.Pagination;
import org.sadtech.bot.bitbucketbot.domain.entity.Comment;
import org.sadtech.bot.bitbucketbot.repository.jpa.CommentRepository;
import org.sadtech.bot.bitbucketbot.service.CommentService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final InitConfig initConfig;

    @Override
    public Long getLastCommentId() {
        return commentRepository.findFirstByOrderByIdDesc().map(Comment::getId).orElse(getInitCommentId());
    }

    private Long getInitCommentId() {
        return initConfig.getStartCommentId() != null ? initConfig.getStartCommentId() : 0L;
    }

    @Override
    public Page<Comment> getAll(@NonNull Pagination pagination) {
        return commentRepository.findAll(PageRequest.of(pagination.getPage(), pagination.getSize()));
    }

    @Override
    public @NonNull List<Comment> getAllBetweenDate(LocalDateTime dateFrom, LocalDateTime dateTo) {
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
