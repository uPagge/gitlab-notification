package org.sadtech.bot.bitbucketbot.repository.jpa;

import org.sadtech.bot.bitbucketbot.domain.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepositoryJpa extends JpaRepository<Task, Long> {

}
