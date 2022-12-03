package dev.struchkov.bot.gitlab.context.repository;

import dev.struchkov.bot.gitlab.context.domain.entity.Note;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
 * @author upagge 08.09.2020
 */
public interface NoteRepository {

    List<Note> findAllByResponsibleIdAndResolved(Long userId, boolean resolved);

    Page<Note> findAllByResolved(boolean resolved, Pageable pagination);

    Optional<Note> findById(Long noteId);
}
