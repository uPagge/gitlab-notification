package org.sadtech.bot.bitbucketbot.service.impl;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.sadtech.bot.bitbucketbot.config.InitConfig;
import org.sadtech.bot.bitbucketbot.domain.Pagination;
import org.sadtech.bot.bitbucketbot.domain.entity.Comment;
import org.sadtech.bot.bitbucketbot.exception.NotFoundException;
import org.sadtech.bot.bitbucketbot.repository.jpa.CommentRepository;
import org.sadtech.bot.bitbucketbot.service.CommentService;
import org.sadtech.bot.bitbucketbot.service.PersonService;
import org.sadtech.bot.bitbucketbot.service.PullRequestsService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final PersonService personService;
    private final CommentRepository commentRepository;
    private final PullRequestsService pullRequestsService;
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
    public Comment create(@NonNull Comment comment) {
        comment.setAuthor(personService.getProxyByLogin(
                comment.getAuthor().getLogin()).orElseThrow(() -> new NotFoundException(""))
        );
        comment.setPullRequest(
                pullRequestsService.getProxyById(comment.getPullRequest().getId()).orElseThrow(() -> new NotFoundException(""))
        );
        return commentRepository.save(comment);
    }

    @Override
    public void delete(@NonNull Long id) {
        commentRepository.deleteById(id);
    }

    @Override
    public Optional<Comment> getProxyById(@NonNull Long id) {
        return Optional.ofNullable(commentRepository.getOne(id));
    }

    @Override
    @Transactional
    public List<Comment> createAll(List<Comment> newComments) {
        return newComments.stream()
                .map(this::create)
                .collect(Collectors.toList());
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
