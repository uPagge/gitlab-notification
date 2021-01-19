package org.sadtech.bot.gitlab.core.service.impl.note;

import lombok.NonNull;
import org.sadtech.bot.gitlab.context.domain.PersonInformation;
import org.sadtech.bot.gitlab.context.domain.entity.Note;
import org.sadtech.bot.gitlab.context.exception.NotFoundException;
import org.sadtech.bot.gitlab.context.repository.NoteRepository;
import org.sadtech.bot.gitlab.context.service.NoteService;
import org.sadtech.bot.gitlab.context.service.NotifyService;
import org.sadtech.bot.gitlab.context.service.PersonService;
import org.springframework.stereotype.Service;

@Service
public class NoteServiceImpl extends AbstractNoteService<Note> implements NoteService {

    private final NoteRepository noteRepository;
    private final PersonService personService;

    public NoteServiceImpl(
            NoteRepository noteRepository,
            NotifyService notifyService,
            PersonInformation personInformation,
            PersonService personService) {
        super(noteRepository, notifyService, personInformation);
        this.noteRepository = noteRepository;
        this.personService = personService;
    }

    @Override
    public Note create(@NonNull Note note) {
        personService.create(note.getAuthor());

        final Note newNote = noteRepository.save(note);
        notificationPersonal(note);
        return newNote;
    }

    @Override
    public Note update(@NonNull Note note) {
        final Note oldNote = noteRepository.findById(note.getId())
                .orElseThrow(() -> new NotFoundException("Комментарий не найден"));

        if (!oldNote.getUpdated().equals(note.getUpdated())) {
            note.setWebUrl(oldNote.getWebUrl());
            return noteRepository.save(note);
        }
//        updateAnswer(oldNote, note);

        return oldNote;
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
    public void link(@NonNull Long noteId, @NonNull Long mergeRequestId) {
        noteRepository.link(noteId, mergeRequestId);
    }

}
