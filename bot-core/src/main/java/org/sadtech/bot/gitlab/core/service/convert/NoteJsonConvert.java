package org.sadtech.bot.gitlab.core.service.convert;

import lombok.RequiredArgsConstructor;
import org.sadtech.bot.gitlab.context.domain.entity.Note;
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
public class NoteJsonConvert implements Converter<NoteJson, Note> {

    private final PersonJsonConverter personConverter;

    @Override
    public Note convert(NoteJson source) {
        final Note note = new Note();
        note.setAuthor(personConverter.convert(source.getAuthor()));
        note.setId(source.getId());
        note.setBody(source.getBody());
        note.setType(source.getType());
        note.setNoteableType(source.getNoteableType());
        note.setCreated(source.getCreated());
        note.setUpdated(source.getUpdated());
        note.setNoteableId(source.getNoteableId());
        note.setNoteableIid(source.getNoteableIid());
        note.setSystem(source.isSystem());
        note.setResolved(source.getResolved());
        if (source.getResolvedBy() != null) {
            note.setResolvedBy(personConverter.convert(source.getResolvedBy()));
        }
        return note;
    }

}
