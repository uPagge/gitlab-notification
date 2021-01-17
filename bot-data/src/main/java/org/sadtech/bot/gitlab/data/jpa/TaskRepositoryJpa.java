package org.sadtech.bot.gitlab.data.jpa;

import org.sadtech.bot.gitlab.context.domain.entity.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepositoryJpa extends JpaRepository<Task, Long> {

    Page<Task> findAllByResolved(boolean resolved, Pageable pageable);

}
