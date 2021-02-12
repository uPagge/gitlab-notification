package org.sadtech.bot.gitlab.data.impl;

import lombok.NonNull;
import org.sadtech.bot.gitlab.context.domain.entity.Note;
import org.sadtech.bot.gitlab.context.repository.NoteRepository;
import org.sadtech.bot.gitlab.data.jpa.NoteRepositoryJpa;
import org.sadtech.haiti.context.page.Pagination;
import org.sadtech.haiti.context.page.Sheet;
import org.sadtech.haiti.database.repository.manager.AbstractSimpleManagerRepository;
import org.sadtech.haiti.database.util.Converter;
import org.springframework.stereotype.Repository;

/**
 * // TODO: 08.09.2020 Добавить описание.
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

}
