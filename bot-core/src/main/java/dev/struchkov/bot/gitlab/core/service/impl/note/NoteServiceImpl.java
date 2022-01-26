package dev.struchkov.bot.gitlab.core.service.impl.note;

import dev.struchkov.bot.gitlab.context.domain.entity.Note;
import dev.struchkov.bot.gitlab.context.repository.NoteRepository;
import dev.struchkov.bot.gitlab.context.service.NoteService;
import dev.struchkov.haiti.context.page.Pagination;
import dev.struchkov.haiti.context.page.Sheet;
import dev.struchkov.haiti.core.service.AbstractSimpleManagerService;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

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

    @Override
    public List<Note> getAllPersonTask(@NonNull Long userId, boolean resolved) {
        return noteRepository.findAllByResponsibleIdAndResolved(userId, resolved);
    }

}
