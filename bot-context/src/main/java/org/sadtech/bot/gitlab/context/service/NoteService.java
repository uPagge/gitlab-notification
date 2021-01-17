package org.sadtech.bot.gitlab.context.service;

import lombok.NonNull;
import org.sadtech.bot.gitlab.context.domain.entity.Note;
import org.sadtech.haiti.context.service.SimpleManagerService;

public interface NoteService extends SimpleManagerService<Note, Long> {

    void link(@NonNull Long noteId, Long mergeRequestId);

}
