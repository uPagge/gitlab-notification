package org.sadtech.bot.vsc.bitbucketbot.context.repository;

import lombok.NonNull;
import org.sadtech.basic.context.repository.SimpleManagerRepository;
import org.sadtech.bot.vsc.bitbucketbot.context.domain.TaskStatus;
import org.sadtech.bot.vsc.bitbucketbot.context.domain.entity.Task;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TaskRepository extends SimpleManagerRepository<Task, Long> {

    Optional<Task> findFirstByOrderByIdDesc();

    List<Task> findByCreateDateBetween(LocalDateTime dateFrom, LocalDateTime dateTo);

    List<Task> findAllByResponsibleAndStatus(@NonNull String responsibleLogin, @NonNull TaskStatus status);

}
