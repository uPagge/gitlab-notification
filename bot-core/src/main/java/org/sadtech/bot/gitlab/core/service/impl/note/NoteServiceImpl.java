package org.sadtech.bot.gitlab.core.service.impl.note;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.sadtech.bot.gitlab.context.domain.entity.Note;
import org.sadtech.bot.gitlab.context.repository.NoteRepository;
import org.sadtech.bot.gitlab.context.service.NoteService;
import org.sadtech.haiti.context.page.Pagination;
import org.sadtech.haiti.context.page.Sheet;
import org.sadtech.haiti.core.service.AbstractSimpleManagerService;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class NoteServiceImpl extends AbstractSimpleManagerService<Note, Long> implements NoteService {


    private final NoteRepository noteRepository;

    public NoteServiceImpl(NoteRepository noteRepository) {
        super(noteRepository);
        this.noteRepository = noteRepository;
    }

    @Override
    public Note create(@NonNull Note note) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Note update(@NonNull Note note) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Sheet<Note> getAllByResolved(boolean resolved, @NonNull Pagination pagination) {
        return noteRepository.findAllByResolved(resolved, pagination);
    }

}
