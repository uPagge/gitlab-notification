package com.tsc.bitbucketbot.repository.jpa;

import com.tsc.bitbucketbot.domain.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    Optional<Comment> findFirstByOrderByIdDesc();

    List<Comment> findByDateBetween(LocalDateTime dateFrom, LocalDateTime dateTo);

}
