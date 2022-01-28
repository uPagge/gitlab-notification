package dev.struchkov.bot.gitlab.data.impl;

import dev.struchkov.bot.gitlab.context.domain.entity.Note;
import dev.struchkov.bot.gitlab.context.repository.NoteRepository;
import dev.struchkov.bot.gitlab.data.jpa.NoteRepositoryJpa;
import dev.struchkov.haiti.context.page.Pagination;
import dev.struchkov.haiti.context.page.Sheet;
import dev.struchkov.haiti.database.repository.manager.AbstractSimpleManagerRepository;
import dev.struchkov.haiti.database.util.Converter;
import lombok.NonNull;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 *
 * @author upagge 08.09.2020
 */
@Repository
public class NoteRepositoryImpl extends AbstractSimpleManagerRepository<Note, Long> implements NoteRepository {

    private final NoteRepositoryJpa repositoryJpa;

    public NoteRepositoryImpl(NoteRepositoryJpa repositoryJpa) {
        super(repositoryJpa);
        this.repositoryJpa = repositoryJpa;
    }

    @Override
    public Sheet<Note> findAllByResolved(boolean resolved, @NonNull Pagination pagination) {
        return Converter.page(
                repositoryJpa.findAllByResolved(resolved, Converter.pagination(pagination))
        );
    }

    @Override
    public List<Note> findAllByResponsibleIdAndResolved(@NonNull Long userId, boolean resolved) {
        return repositoryJpa.findAllByDiscussionResponsibleIdAndResolved(userId, resolved);
    }


}
