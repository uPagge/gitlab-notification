package org.sadtech.bot.gitlab.core.service.convert;

import org.sadtech.bot.gitlab.context.domain.entity.Note;
import org.sadtech.bot.gitlab.context.domain.entity.Task;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * // TODO: 12.09.2020 Добавить описание.
 *
 * @author upagge 12.09.2020
 */
@Component
public class TaskToCommentConvert implements Converter<Task, Note> {

    @Override
    public Note convert(Task source) {
        final Note note = new Note();
//        note.setId(source.getId());
//        note.setUrl(source.getUrl());
//        note.setUrlApi(source.getUrlApi());
//        note.setPullRequestId(source.getPullRequestId());
//        note.setBitbucketVersion(source.getBitbucketVersion());
//        note.setCreateDate(source.getCreateDate());
//        note.setMessage(source.getDescription());
//        note.setResponsible(source.getResponsible());
//        note.setAuthor(source.getAuthor());
//        note.setAnswers(source.getAnswers());
        return note;
    }

}
