package org.sadtech.bot.gitlab.data.jpa;

import lombok.NonNull;
import org.sadtech.bot.gitlab.context.domain.entity.Note;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@NoRepositoryBean
public interface CommentRepositoryJpa extends JpaRepository<Note, Long> {

    Optional<Note> findFirstByOrderByIdDesc();

    List<Note> findByCreateDateBetween(@NonNull LocalDateTime dateFrom, @NonNull LocalDateTime dateTo);

    //    @Query("SELECT c.id FROM Comment c WHERE c.id IN :ids")
    Set<Long> existsAllById(@NonNull Set<Long> ids);

}
