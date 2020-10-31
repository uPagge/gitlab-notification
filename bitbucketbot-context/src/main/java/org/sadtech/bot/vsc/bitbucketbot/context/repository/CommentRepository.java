package org.sadtech.bot.vsc.bitbucketbot.context.repository;

import lombok.NonNull;
import org.sadtech.basic.context.repository.SimpleManagerRepository;
import org.sadtech.bot.vsc.bitbucketbot.context.domain.entity.Comment;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * // TODO: 08.09.2020 Добавить описание.
 *
 * @author upagge 08.09.2020
 */
public interface CommentRepository extends SimpleManagerRepository<Comment, Long> {

    Optional<Comment> findFirstByOrderByIdDesc();

    List<Comment> findByCreateDateBetween(@NonNull LocalDateTime dateFrom, @NonNull LocalDateTime dateTo);

    List<Comment> findAllById(@NonNull Set<Long> ids);

    Set<Long> existsById(Set<Long> ids);

}
