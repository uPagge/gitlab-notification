package org.sadtech.bot.bitbucketbot.repository.jpa;

import org.sadtech.bot.bitbucketbot.domain.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TaskRepositoryJpa extends JpaRepository<Task, Long> {

    Optional<Task> findFirstByOrderByIdDesc();

}
