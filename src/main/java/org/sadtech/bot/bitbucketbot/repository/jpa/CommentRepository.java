package org.sadtech.bot.bitbucketbot.repository.jpa;

import org.sadtech.bot.bitbucketbot.domain.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    Optional<Comment> findFirstByOrderByIdDesc();

    List<Comment> findByCreateDateBetween(LocalDateTime dateFrom, LocalDateTime dateTo);

}
