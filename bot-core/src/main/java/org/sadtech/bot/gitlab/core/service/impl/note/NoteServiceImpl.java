package org.sadtech.bot.gitlab.core.service.impl.note;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.sadtech.bot.gitlab.context.domain.PersonInformation;
import org.sadtech.bot.gitlab.context.domain.entity.Note;
import org.sadtech.bot.gitlab.context.domain.notify.comment.CommentNotify;
import org.sadtech.bot.gitlab.context.exception.NotFoundException;
import org.sadtech.bot.gitlab.context.repository.NoteRepository;
import org.sadtech.bot.gitlab.context.service.NoteService;
import org.sadtech.bot.gitlab.context.service.NotifyService;
import org.sadtech.bot.gitlab.context.service.PersonService;
import org.sadtech.haiti.context.page.Pagination;
import org.sadtech.haiti.context.page.Sheet;
import org.sadtech.haiti.core.service.AbstractSimpleManagerService;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
public class NoteServiceImpl extends AbstractSimpleManagerService<Note, Long> implements NoteService {

    protected static final Pattern PATTERN = Pattern.compile("@[\\w]+");

    private final NoteRepository noteRepository;
    private final PersonService personService;

    private final NotifyService notifyService;
    private final PersonInformation personInformation;

    public NoteServiceImpl(
            NoteRepository noteRepository,
            PersonService personService,
            NotifyService notifyService,
            PersonInformation personInformation
    ) {
        super(noteRepository);
        this.noteRepository = noteRepository;
        this.personService = personService;
        this.notifyService = notifyService;
        this.personInformation = personInformation;
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

        return oldNote;
    }

    @Override
    public Sheet<Note> getAllByResolved(boolean resolved, @NonNull Pagination pagination) {
        return noteRepository.findAllByResolved(resolved, pagination);
    }

    @Override
    public List<Note> getAllPersonTask(@NonNull Long userId, boolean resolved) {
        return noteRepository.findAllByResponsibleIdAndResolved(userId, resolved);
    }

    protected void notificationPersonal(@NonNull Note note) {
        Matcher matcher = PATTERN.matcher(note.getBody());
        Set<String> recipientsLogins = new HashSet<>();
        while (matcher.find()) {
            final String login = matcher.group(0).replace("@", "");
            recipientsLogins.add(login);
        }
        if (recipientsLogins.contains(personInformation.getUsername())) {
            notifyService.send(
                    CommentNotify.builder()
                            .authorName(note.getAuthor().getName())
                            .message(note.getBody())
                            .url(note.getWebUrl())
                            .build()
            );
        }
    }

}
