package org.sadtech.bot.bitbucketbot.service.impl;

import lombok.NonNull;
import org.sadtech.basic.core.service.AbstractSimpleManagerService;
import org.sadtech.bot.bitbucketbot.domain.change.comment.CommentChange;
import org.sadtech.bot.bitbucketbot.domain.entity.Comment;
import org.sadtech.bot.bitbucketbot.exception.NotFoundException;
import org.sadtech.bot.bitbucketbot.repository.CommentRepository;
import org.sadtech.bot.bitbucketbot.service.ChangeService;
import org.sadtech.bot.bitbucketbot.service.CommentService;
import org.sadtech.bot.bitbucketbot.service.PersonService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class CommentServiceImpl extends AbstractSimpleManagerService<Comment, Long> implements CommentService {

    private static final Pattern PATTERN = Pattern.compile("@[\\w]+");

    private final CommentRepository commentRepository;
    private final PersonService personService;
    private final ChangeService changeService;

    public CommentServiceImpl(CommentRepository commentRepository, PersonService personService, ChangeService changeService) {
        super(commentRepository);
        this.personService = personService;
        this.commentRepository = commentRepository;
        this.changeService = changeService;
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
            return commentRepository.save(oldComment);
        }

        return oldComment;
    }

    @Override
    public List<Comment> getAllById(@NonNull Set<Long> ids) {
        return commentRepository.findAllById(ids);
    }

}
