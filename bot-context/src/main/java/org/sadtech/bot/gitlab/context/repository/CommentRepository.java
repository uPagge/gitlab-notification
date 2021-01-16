package org.sadtech.bot.gitlab.context.repository;

import lombok.NonNull;
import org.sadtech.bot.gitlab.context.domain.entity.Note;
import org.sadtech.haiti.context.repository.SimpleManagerRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * // TODO: 08.09.2020 Добавить описание.
 *
 * @author upagge 08.09.2020
 */
public interface CommentRepository extends SimpleManagerRepository<Note, Long> {

    Optional<Note> findFirstByOrderByIdDesc();

    List<Note> findByCreateDateBetween(@NonNull LocalDateTime dateFrom, @NonNull LocalDateTime dateTo);

    List<Note> findAllById(@NonNull Set<Long> ids);

    Set<Long> existsById(Set<Long> ids);

}
