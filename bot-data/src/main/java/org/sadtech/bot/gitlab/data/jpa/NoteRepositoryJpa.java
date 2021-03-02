package org.sadtech.bot.gitlab.data.jpa;

import org.sadtech.bot.gitlab.context.domain.entity.Note;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NoteRepositoryJpa extends JpaRepository<Note, Long> {

    Page<Note> findAllByResolved(boolean resolved, Pageable pageable);

    List<Note> findAllByDiscussionResponsibleIdAndResolved(Long responsibleId, boolean resolved);

}
