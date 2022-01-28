package dev.struchkov.bot.gitlab.context.service;

import dev.struchkov.bot.gitlab.context.domain.entity.Note;
import dev.struchkov.haiti.context.page.Pagination;
import dev.struchkov.haiti.context.page.Sheet;
import dev.struchkov.haiti.context.service.SimpleManagerService;
import lombok.NonNull;

import java.util.List;

public interface NoteService extends SimpleManagerService<Note, Long> {

    List<Note> getAllPersonTask(@NonNull Long userId, boolean resolved);

    //TODO [28.01.2022]: Решить нужно ли оставлять
    Sheet<Note> getAllByResolved(boolean resolved, @NonNull Pagination pagination);

}
