package org.sadtech.bot.gitlab.data.jpa;

import lombok.NonNull;
import org.sadtech.bot.gitlab.context.domain.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface CommentRepositoryJpa extends JpaRepository<Comment, Long> {

    Optional<Comment> findFirstByOrderByIdDesc();

    List<Comment> findByCreateDateBetween(@NonNull LocalDateTime dateFrom, @NonNull LocalDateTime dateTo);

    @Query("SELECT c.id FROM Comment c WHERE c.id IN :ids")
    Set<Long> existsAllById(@NonNull Set<Long> ids);

}
