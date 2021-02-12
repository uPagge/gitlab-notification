package org.sadtech.bot.gitlab.context.repository;

import lombok.NonNull;
import org.sadtech.bot.gitlab.context.domain.entity.Note;
import org.sadtech.haiti.context.page.Pagination;
import org.sadtech.haiti.context.page.Sheet;
import org.sadtech.haiti.context.repository.SimpleManagerRepository;

/**
 * // TODO: 08.09.2020 Добавить описание.
 *
 * @author upagge 08.09.2020
 */
public interface NoteRepository extends SimpleManagerRepository<Note, Long> {

    Sheet<Note> findAllByResolved(boolean resolved, @NonNull Pagination pagination);

}
