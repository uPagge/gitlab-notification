package org.sadtech.bot.gitlab.context.service;

import lombok.NonNull;
import org.sadtech.bot.gitlab.context.domain.entity.Note;
import org.sadtech.haiti.context.page.Pagination;
import org.sadtech.haiti.context.page.Sheet;
import org.sadtech.haiti.context.service.SimpleManagerService;

public interface NoteService extends SimpleManagerService<Note, Long> {

    Sheet<Note> getAllByResolved(boolean resolved, @NonNull Pagination pagination);

}
