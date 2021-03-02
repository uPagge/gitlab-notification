package org.sadtech.bot.gitlab.context.repository;

import lombok.NonNull;
import org.sadtech.bot.gitlab.context.domain.entity.Note;
import org.sadtech.haiti.context.page.Pagination;
import org.sadtech.haiti.context.page.Sheet;
import org.sadtech.haiti.context.repository.SimpleManagerRepository;

import java.util.List;

/**
 * // TODO: 08.09.2020 Добавить описание.
 *
 * @author upagge 08.09.2020
 */
public interface NoteRepository extends SimpleManagerRepository<Note, Long> {

    List<Note> findAllByResponsibleIdAndResolved(@NonNull Long userId, boolean resolved);

    Sheet<Note> findAllByResolved(boolean resolved, @NonNull Pagination pagination);

}
