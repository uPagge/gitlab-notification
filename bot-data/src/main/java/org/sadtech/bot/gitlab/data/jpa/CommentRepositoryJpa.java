package org.sadtech.bot.gitlab.data.jpa;

import org.sadtech.bot.gitlab.context.domain.entity.Note;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepositoryJpa extends JpaRepository<Note, Long> {

}
