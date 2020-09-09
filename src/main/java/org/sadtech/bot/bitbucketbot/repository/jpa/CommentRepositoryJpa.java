package org.sadtech.bot.bitbucketbot.repository.jpa;

import lombok.NonNull;
import org.sadtech.bot.bitbucketbot.domain.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface CommentRepositoryJpa extends JpaRepository<Comment, Long> {

    Optional<Comment> findFirstByOrderByIdDesc();

    List<Comment> findByCreateDateBetween(@NonNull LocalDateTime dateFrom, @NonNull LocalDateTime dateTo);

}
