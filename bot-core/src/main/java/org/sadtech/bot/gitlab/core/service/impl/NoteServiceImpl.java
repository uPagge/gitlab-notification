package org.sadtech.bot.gitlab.core.service.impl;

import lombok.NonNull;
import org.sadtech.bot.gitlab.context.domain.entity.Note;
import org.sadtech.bot.gitlab.context.domain.entity.Task;
import org.sadtech.bot.gitlab.context.domain.notify.comment.CommentNotify;
import org.sadtech.bot.gitlab.context.exception.NotFoundException;
import org.sadtech.bot.gitlab.context.repository.CommentRepository;
import org.sadtech.bot.gitlab.context.service.NoteService;
import org.sadtech.bot.gitlab.context.service.NotifyService;
import org.sadtech.bot.gitlab.context.service.TaskService;
import org.sadtech.haiti.context.domain.ExistsContainer;
import org.sadtech.haiti.core.service.AbstractSimpleManagerService;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class NoteServiceImpl extends AbstractSimpleManagerService<Note, Long> implements NoteService {

    private static final Pattern PATTERN = Pattern.compile("@[\\w]+");

    private final CommentRepository commentRepository;
    private final NotifyService notifyService;
    private final TaskService taskService;

    private final ConversionService conversionService;

    public NoteServiceImpl(
            CommentRepository commentRepository,
            NotifyService notifyService,
            @Lazy TaskService taskService,
            ConversionService conversionService
    ) {
        super(commentRepository);
        this.commentRepository = commentRepository;
        this.notifyService = notifyService;
        this.taskService = taskService;
        this.conversionService = conversionService;
    }

    @Override
    public Long getLastCommentId() {
        return commentRepository.findFirstByOrderByIdDesc().map(Note::getId).orElse(0L);
    }

    @Override
    public List<Note> getAllBetweenDate(@NonNull LocalDateTime dateFrom, LocalDateTime dateTo) {
        return commentRepository.findByCreateDateBetween(dateFrom, dateTo);
    }

    @Override
    public Note create(@NonNull Note note) {
        final Note newNote = commentRepository.save(note);
        notificationPersonal(note);
        return newNote;
    }

    private void notificationPersonal(@NonNull Note note) {
        Matcher matcher = PATTERN.matcher(note.getBody());
        Set<String> recipientsLogins = new HashSet<>();
        while (matcher.find()) {
            final String login = matcher.group(0).replace("@", "");
            recipientsLogins.add(login);
        }
        notifyService.send(
                CommentNotify.builder()
//                        .authorName(note.getAuthor())
//                        .url(note.getUrl())
//                        .message(note.getMessage())
                        .build()
        );
    }

    @Override
    public Note update(Note note) {
        final Note oldNote = commentRepository.findById(note.getId())
                .orElseThrow(() -> new NotFoundException("Комментарий не найден"));

        updateAnswer(oldNote, note);

        return commentRepository.save(oldNote);
    }

    @Override
    public List<Note> getAllById(@NonNull Set<Long> ids) {
        return commentRepository.findAllById(ids);
    }

    @Override
    public Note convert(@NonNull Task task) {
        taskService.deleteById(task.getId());
        final Note note = conversionService.convert(task, Note.class);
        return commentRepository.save(note);
    }

    @Override
    public Set<Long> existsById(@NonNull Set<Long> ids) {
        return commentRepository.existsById(ids);
    }

    private void updateAnswer(Note oldNote, Note newNote) {
//        final Set<Long> oldAnswerIds = oldNote.getAnswers();
//        final Set<Long> newAnswerIds = newNote.getAnswers();
//        if (!oldAnswerIds.equals(newAnswerIds)) {
//            final Set<Long> existsNewAnswersIds = commentRepository.existsById(newAnswerIds);
//            final List<Note> newAnswers = commentRepository.findAllById(existsNewAnswersIds).stream()
//                    .filter(comment -> !oldAnswerIds.contains(comment.getId()))
//                    .collect(Collectors.toList());
//            oldNote.getAnswers().clear();
//            oldNote.setAnswers(existsNewAnswersIds);
//            if (!newAnswers.isEmpty()) {
//                notifyService.send(
//                        AnswerCommentNotify.builder()
//                                .url(oldNote.getUrl())
//                                .youMessage(newNote.getMessage())
//                                .answers(
//                                        newAnswers.stream()
//                                                .map(answerComment -> Answer.of(answerComment.getAuthor(), answerComment.getMessage()))
//                                                .collect(Collectors.toList())
//                                )
//                                .build()
//                );
//            }
//        }
    }

    @Override
    public ExistsContainer<Note, Long> existsById(@NonNull Collection<Long> collection) {
        return null;
    }

}
