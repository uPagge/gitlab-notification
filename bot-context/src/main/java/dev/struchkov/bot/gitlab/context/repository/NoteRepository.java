package dev.struchkov.bot.gitlab.context.repository;

import dev.struchkov.bot.gitlab.context.domain.entity.Note;
import dev.struchkov.haiti.context.page.Pagination;
import dev.struchkov.haiti.context.page.Sheet;
import dev.struchkov.haiti.context.repository.SimpleManagerRepository;
import lombok.NonNull;

import java.util.List;

/**
 * @author upagge 08.09.2020
 */
public interface NoteRepository extends SimpleManagerRepository<Note, Long> {

    List<Note> findAllByResponsibleIdAndResolved(@NonNull Long userId, boolean resolved);

    Sheet<Note> findAllByResolved(boolean resolved, @NonNull Pagination pagination);

}
