package org.sadtech.bot.gitlab.core.service.impl;

import lombok.NonNull;
import org.sadtech.bot.gitlab.context.domain.Answer;
import org.sadtech.bot.gitlab.context.domain.PointType;
import org.sadtech.bot.gitlab.context.domain.entity.Comment;
import org.sadtech.bot.gitlab.context.domain.entity.Task;
import org.sadtech.bot.gitlab.context.domain.notify.comment.AnswerCommentNotify;
import org.sadtech.bot.gitlab.context.domain.notify.comment.CommentNotify;
import org.sadtech.bot.gitlab.context.exception.NotFoundException;
import org.sadtech.bot.gitlab.context.repository.CommentRepository;
import org.sadtech.bot.gitlab.context.service.CommentService;
import org.sadtech.bot.gitlab.context.service.NotifyService;
import org.sadtech.bot.gitlab.context.service.RatingService;
import org.sadtech.bot.gitlab.context.service.TaskService;
import org.sadtech.haiti.core.service.AbstractSimpleManagerService;
import org.sadtech.haiti.core.util.Assert;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class CommentServiceImpl extends AbstractSimpleManagerService<Comment, Long> implements CommentService {

    private static final Pattern PATTERN = Pattern.compile("@[\\w]+");

    private final CommentRepository commentRepository;
    private final NotifyService notifyService;
    private final TaskService taskService;
    private final RatingService ratingService;

    private final ConversionService conversionService;

    public CommentServiceImpl(
            CommentRepository commentRepository,
            NotifyService notifyService,
            @Lazy TaskService taskService,
            RatingService ratingService,
            ConversionService conversionService
    ) {
        super(commentRepository);
        this.commentRepository = commentRepository;
        this.notifyService = notifyService;
        this.taskService = taskService;
        this.ratingService = ratingService;
        this.conversionService = conversionService;
    }

    @Override
    public Long getLastCommentId() {
        return commentRepository.findFirstByOrderByIdDesc().map(Comment::getId).orElse(0L);
    }

    @Override
    public List<Comment> getAllBetweenDate(@NonNull LocalDateTime dateFrom, LocalDateTime dateTo) {
        return commentRepository.findByCreateDateBetween(dateFrom, dateTo);
    }

    @Override
    public Comment create(@NonNull Comment comment) {
        Assert.isNotNull(comment.getId(), "При создании объекта должен быть установлен идентификатор");
        comment.getAnswers().clear();
        final Comment newComment = commentRepository.save(comment);
        ratingCreateComment(comment.getAuthor());
        notificationPersonal(comment);
        return newComment;
    }

    private void ratingCreateComment(String author) {
        ratingService.addRating(author, PointType.COMMENT_ADD, PointType.COMMENT_ADD.getPoints());
    }

    private void notificationPersonal(@NonNull Comment comment) {
        Matcher matcher = PATTERN.matcher(comment.getMessage());
        Set<String> recipientsLogins = new HashSet<>();
        while (matcher.find()) {
            final String login = matcher.group(0).replace("@", "");
            recipientsLogins.add(login);
        }
        notifyService.send(
                CommentNotify.builder()
                        .authorName(comment.getAuthor())
                        .url(comment.getUrl())
                        .recipients(recipientsLogins)
                        .message(comment.getMessage())
                        .build()
        );
    }

    @Override
    public Comment update(Comment comment) {
        final Comment oldComment = commentRepository.findById(comment.getId())
                .orElseThrow(() -> new NotFoundException("Комментарий не найден"));

        if (oldComment.getBitbucketVersion().equals(comment.getBitbucketVersion())) {
            oldComment.setBitbucketVersion(comment.getBitbucketVersion());
            oldComment.setMessage(oldComment.getMessage());
        }
        updateAnswer(oldComment, comment);

        return commentRepository.save(oldComment);
    }

    @Override
    public List<Comment> getAllById(@NonNull Set<Long> ids) {
        return commentRepository.findAllById(ids);
    }

    @Override
    public Comment convert(@NonNull Task task) {
        taskService.deleteById(task.getId());
        final Comment comment = conversionService.convert(task, Comment.class);
        final Comment newComment = commentRepository.save(comment);
        ratingService.addRating(newComment.getAuthor(), PointType.COMMENT_ADD, PointType.COMMENT_ADD.getPoints());
        return newComment;
    }

    @Override
    public Set<Long> existsById(@NonNull Set<Long> ids) {
        return commentRepository.existsById(ids);
    }

    private void updateAnswer(Comment oldComment, Comment newComment) {
        final Set<Long> oldAnswerIds = oldComment.getAnswers();
        final Set<Long> newAnswerIds = newComment.getAnswers();
        if (!oldAnswerIds.equals(newAnswerIds)) {
            final Set<Long> existsNewAnswersIds = commentRepository.existsById(newAnswerIds);
            final List<Comment> newAnswers = commentRepository.findAllById(existsNewAnswersIds).stream()
                    .filter(comment -> !oldAnswerIds.contains(comment.getId()))
                    .collect(Collectors.toList());
            oldComment.getAnswers().clear();
            oldComment.setAnswers(existsNewAnswersIds);
            if (!newAnswers.isEmpty()) {
                notifyService.send(
                        AnswerCommentNotify.builder()
                                .recipients(Collections.singleton(newComment.getAuthor()))
                                .url(oldComment.getUrl())
                                .youMessage(newComment.getMessage())
                                .answers(
                                        newAnswers.stream()
                                                .map(answerComment -> Answer.of(answerComment.getAuthor(), answerComment.getMessage()))
                                                .collect(Collectors.toList())
                                )
                                .build()
                );
            }
        }
    }

    @Override
    public void deleteById(@NonNull Long id) {
        final Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Комментарий не найден"));
        ratingService.addRating(comment.getAuthor(), PointType.COMMENT_DELETE, PointType.COMMENT_DELETE.getPoints());
        super.deleteById(id);
    }
}
