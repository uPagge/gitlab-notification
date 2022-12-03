package dev.struchkov.bot.gitlab.data.impl;

import dev.struchkov.bot.gitlab.context.domain.entity.Note;
import dev.struchkov.bot.gitlab.context.repository.NoteRepository;
import dev.struchkov.bot.gitlab.data.jpa.NoteJpaRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * @author upagge 08.09.2020
 */
@Repository
@RequiredArgsConstructor
public class NoteRepositoryImpl implements NoteRepository {

    private final NoteJpaRepository jpaRepository;

    @Override
    public Page<Note> findAllByResolved(boolean resolved, @NonNull Pageable pagination) {
        return jpaRepository.findAllByResolved(resolved, pagination);
    }

    @Override
    public Optional<Note> findById(Long noteId) {
        return jpaRepository.findById(noteId);
    }

    @Override
    public List<Note> findAllByResponsibleIdAndResolved(@NonNull Long userId, boolean resolved) {
        return jpaRepository.findAllByDiscussionResponsibleIdAndResolved(userId, resolved);
    }


}
