package org.sadtech.bot.gitlab.data.jpa;

import org.sadtech.bot.gitlab.context.domain.entity.Note;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface CommentRepositoryJpa extends JpaRepository<Note, Long> {

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO gitlab_bot.public.merge_request_notes(merge_request_id, notes_id) values (:mergeRequestId, :noteId)", nativeQuery = true)
    void link(@Param("noteId") Long noteId, @Param("mergeRequestId") Long mergeRequestId);

}
