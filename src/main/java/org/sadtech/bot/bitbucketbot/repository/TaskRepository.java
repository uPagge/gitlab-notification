package org.sadtech.bot.bitbucketbot.repository;

import org.sadtech.basic.context.repository.SimpleManagerRepository;
import org.sadtech.bot.bitbucketbot.domain.entity.Task;

import java.util.Optional;

public interface TaskRepository extends SimpleManagerRepository<Task, Long> {

    Optional<Task> findFirstByOrderByIdDesc();

}
