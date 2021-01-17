package org.sadtech.bot.gitlab.data.impl;

import org.sadtech.bot.gitlab.context.domain.entity.Note;
import org.sadtech.bot.gitlab.context.repository.NoteRepository;
import org.sadtech.bot.gitlab.data.jpa.CommentRepositoryJpa;
import org.sadtech.haiti.database.repository.manager.AbstractSimpleManagerRepository;
import org.springframework.stereotype.Repository;

/**
 * // TODO: 08.09.2020 Добавить описание.
 *
 * @author upagge 08.09.2020
 */
@Repository
public class NoteRepositoryImpl extends AbstractSimpleManagerRepository<Note, Long> implements NoteRepository {

    private final CommentRepositoryJpa repositoryJpa;

    public NoteRepositoryImpl(CommentRepositoryJpa repositoryJpa) {
        super(repositoryJpa);
        this.repositoryJpa = repositoryJpa;
    }

}
