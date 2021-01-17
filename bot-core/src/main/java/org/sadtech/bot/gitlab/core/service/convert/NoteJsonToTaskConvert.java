package org.sadtech.bot.gitlab.core.service.convert;

import lombok.RequiredArgsConstructor;
import org.sadtech.bot.gitlab.context.domain.entity.Task;
import org.sadtech.bot.gitlab.sdk.domain.NoteJson;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * // TODO: 12.09.2020 Добавить описание.
 *
 * @author upagge 12.09.2020
 */
@Component
@RequiredArgsConstructor
public class NoteJsonToTaskConvert implements Converter<NoteJson, Task> {

    private final PersonJsonConverter personConverter;

    @Override
    public Task convert(NoteJson source) {
        final Task task = new Task();
        task.setAuthor(personConverter.convert(source.getAuthor()));
        task.setId(source.getId());
        task.setBody(source.getBody());
        task.setType(source.getType());
        task.setNoteableType(source.getNoteableType());
        task.setCreated(source.getCreated());
        task.setUpdated(source.getUpdated());
        task.setNoteableId(source.getNoteableId());
        task.setNoteableIid(source.getNoteableIid());
        task.setSystem(source.isSystem());

        task.setResolved(source.getResolved());
        if (source.getResolvedBy() != null) {
            task.setResolvedBy(personConverter.convert(source.getResolvedBy()));
        }
        return task;
    }

}
