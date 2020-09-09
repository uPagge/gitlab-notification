package org.sadtech.bot.bitbucketbot.service.impl;

import lombok.NonNull;
import org.sadtech.basic.core.service.AbstractSimpleManagerService;
import org.sadtech.bot.bitbucketbot.config.InitProperty;
import org.sadtech.bot.bitbucketbot.domain.entity.Comment;
import org.sadtech.bot.bitbucketbot.exception.NotFoundException;
import org.sadtech.bot.bitbucketbot.repository.CommentRepository;
import org.sadtech.bot.bitbucketbot.service.CommentService;
import org.sadtech.bot.bitbucketbot.service.PersonService;
import org.sadtech.bot.bitbucketbot.service.PullRequestsService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Service
public class CommentServiceImpl extends AbstractSimpleManagerService<Comment, Long> implements CommentService {

    private final CommentRepository commentRepository;
    private final PersonService personService;
    private final PullRequestsService pullRequestsService;
    private final InitProperty initProperty;

    public CommentServiceImpl(CommentRepository commentRepository, PersonService personService, PullRequestsService pullRequestsService, InitProperty initProperty) {
        super(commentRepository);
        this.personService = personService;
        this.commentRepository = commentRepository;
        this.pullRequestsService = pullRequestsService;
        this.initProperty = initProperty;
    }

    @Override
    public Long getLastCommentId() {
        return commentRepository.findFirstByOrderByIdDesc().map(Comment::getId).orElse(getInitCommentId());
    }

    private Long getInitCommentId() {
        return initProperty.getStartCommentId() != null ? initProperty.getStartCommentId() : 0L;
    }

    @Override
    public List<Comment> getAllBetweenDate(@NonNull LocalDateTime dateFrom, LocalDateTime dateTo) {
        return commentRepository.findByCreateDateBetween(dateFrom, dateTo);
    }

    @Override
    public Comment create(@NonNull Comment comment) {
        return commentRepository.save(comment);
    }

    @Override
    public Comment update(Comment comment) {
        final Comment oldComment = commentRepository.findById(comment.getId())
                .orElseThrow(() -> new NotFoundException("Комментарий не найден"));

        if (oldComment.getBitbucketVersion().equals(comment.getBitbucketVersion())) {
            oldComment.setBitbucketVersion(comment.getBitbucketVersion());
            oldComment.setMessage(oldComment.getMessage());
            return commentRepository.save(oldComment);
        }

        return oldComment;
    }

    @Override
    public List<Comment> getAllById(@NonNull Set<Long> ids) {
        return commentRepository.findAllById(ids);
    }

}
