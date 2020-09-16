package org.sadtech.bot.bitbucketbot.service.impl;

import lombok.NonNull;
import org.sadtech.basic.core.service.AbstractSimpleManagerService;
import org.sadtech.bot.bitbucketbot.domain.Answer;
import org.sadtech.bot.bitbucketbot.domain.change.comment.AnswerCommentChange;
import org.sadtech.bot.bitbucketbot.domain.change.comment.CommentChange;
import org.sadtech.bot.bitbucketbot.domain.entity.Comment;
import org.sadtech.bot.bitbucketbot.domain.entity.Task;
import org.sadtech.bot.bitbucketbot.exception.NotFoundException;
import org.sadtech.bot.bitbucketbot.repository.CommentRepository;
import org.sadtech.bot.bitbucketbot.service.ChangeService;
import org.sadtech.bot.bitbucketbot.service.CommentService;
import org.sadtech.bot.bitbucketbot.service.PersonService;
import org.sadtech.bot.bitbucketbot.service.TaskService;
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
    private final PersonService personService;
    private final ChangeService changeService;
    private final TaskService taskService;

    private final ConversionService conversionService;

    public CommentServiceImpl(
            CommentRepository commentRepository,
            PersonService personService,
            ChangeService changeService,
            @Lazy TaskService taskService,
            ConversionService conversionService
    ) {
        super(commentRepository);
        this.personService = personService;
        this.commentRepository = commentRepository;
        this.changeService = changeService;
        this.taskService = taskService;
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
        comment.getAnswers().clear();
        final Comment newComment = commentRepository.save(comment);
        notificationPersonal(comment);
        return newComment;
    }

    private void notificationPersonal(@NonNull Comment comment) {
        Matcher matcher = PATTERN.matcher(comment.getMessage());
        Set<String> recipientsLogins = new HashSet<>();
        while (matcher.find()) {
            final String login = matcher.group(0).replace("@", "");
            recipientsLogins.add(login);
        }
        final Set<Long> recipientsIds = personService.getAllTelegramIdByLogin(recipientsLogins);
        changeService.save(
                CommentChange.builder()
                        .authorName(comment.getAuthor())
                        .url(comment.getUrl())
                        .telegramIds(recipientsIds)
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
        notificationPersonal(newComment);
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
            changeService.save(
                    AnswerCommentChange.builder()
                            .telegramIds(
                                    personService.getAllTelegramIdByLogin(Collections.singleton(newComment.getAuthor()))
                            )
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
