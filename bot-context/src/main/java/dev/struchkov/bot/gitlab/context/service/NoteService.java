package dev.struchkov.bot.gitlab.context.service;

import dev.struchkov.bot.gitlab.context.domain.entity.Note;
import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface NoteService {

    List<Note> getAllPersonTask(@NonNull Long userId, boolean resolved);

    //TODO [28.01.2022]: Решить нужно ли оставлять
    Page<Note> getAllByResolved(boolean resolved, @NonNull Pageable pagination);

    Note getByIdOrThrow(@NonNull Long noteId);

}
