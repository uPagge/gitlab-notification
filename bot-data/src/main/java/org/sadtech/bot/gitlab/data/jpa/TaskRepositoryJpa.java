package org.sadtech.bot.gitlab.data.jpa;

import lombok.NonNull;
import org.sadtech.bot.gitlab.context.domain.TaskStatus;
import org.sadtech.bot.gitlab.context.domain.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TaskRepositoryJpa extends JpaRepository<Task, Long> {

    Optional<Task> findFirstByOrderByIdDesc();

    List<Task> findByCreateDateBetween(@NonNull LocalDateTime dateFrom, @NonNull LocalDateTime dateTo);

    List<Task> findAllByResponsibleAndStatus(String login, TaskStatus taskStatus);

}
