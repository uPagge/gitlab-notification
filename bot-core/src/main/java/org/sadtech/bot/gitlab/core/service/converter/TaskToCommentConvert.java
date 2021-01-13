package org.sadtech.bot.gitlab.core.service.converter;

import org.sadtech.bot.gitlab.context.domain.entity.Comment;
import org.sadtech.bot.gitlab.context.domain.entity.Task;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * // TODO: 12.09.2020 Добавить описание.
 *
 * @author upagge 12.09.2020
 */
@Component
public class TaskToCommentConvert implements Converter<Task, Comment> {

    @Override
    public Comment convert(Task source) {
        final Comment comment = new Comment();
        comment.setId(source.getId());
        comment.setUrl(source.getUrl());
        comment.setUrlApi(source.getUrlApi());
        comment.setPullRequestId(source.getPullRequestId());
        comment.setBitbucketVersion(source.getBitbucketVersion());
        comment.setCreateDate(source.getCreateDate());
        comment.setMessage(source.getDescription());
        comment.setResponsible(source.getResponsible());
        comment.setAuthor(source.getAuthor());
        comment.setAnswers(source.getAnswers());
        return comment;
    }

}
