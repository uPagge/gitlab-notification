package org.sadtech.bot.gitlab.context.service;

import lombok.NonNull;
import org.sadtech.bot.gitlab.context.domain.entity.Note;
import org.sadtech.bot.gitlab.context.domain.entity.Task;
import org.sadtech.haiti.context.service.SimpleManagerService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public interface NoteService extends SimpleManagerService<Note, Long> {

    Long getLastCommentId();

    List<Note> getAllBetweenDate(@NonNull LocalDateTime dateFrom, @NonNull LocalDateTime dateTo);

    List<Note> getAllById(@NonNull Set<Long> ids);

    Note convert(@NonNull Task task);

}
