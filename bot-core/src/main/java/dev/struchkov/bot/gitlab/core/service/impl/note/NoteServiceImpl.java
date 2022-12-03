package dev.struchkov.bot.gitlab.core.service.impl.note;

import dev.struchkov.bot.gitlab.context.domain.entity.Note;
import dev.struchkov.bot.gitlab.context.repository.NoteRepository;
import dev.struchkov.bot.gitlab.context.service.NoteService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

import static dev.struchkov.haiti.context.exception.NotFoundException.notFoundException;

@Slf4j
@Service
@RequiredArgsConstructor
public class NoteServiceImpl implements NoteService {

    private final NoteRepository noteRepository;

    @Override
    public Page<Note> getAllByResolved(boolean resolved, @NonNull Pageable pagination) {
        return noteRepository.findAllByResolved(resolved, pagination);
    }

    @Override
    public Note getByIdOrThrow(@NonNull Long noteId) {
        return noteRepository.findById(noteId)
                .orElseThrow(notFoundException("Note не найдено"));
    }

    @Override
    public List<Note> getAllPersonTask(@NonNull Long userId, boolean resolved) {
        return noteRepository.findAllByResponsibleIdAndResolved(userId, resolved);
    }

}
