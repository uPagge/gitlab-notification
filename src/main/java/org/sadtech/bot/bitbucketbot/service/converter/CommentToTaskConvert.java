package org.sadtech.bot.bitbucketbot.service.converter;

import org.sadtech.bot.bitbucketbot.domain.TaskStatus;
import org.sadtech.bot.bitbucketbot.domain.entity.Comment;
import org.sadtech.bot.bitbucketbot.domain.entity.Task;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * // TODO: 12.09.2020 Добавить описание.
 *
 * @author upagge 12.09.2020
 */
@Component
public class CommentToTaskConvert implements Converter<Comment, Task> {

    @Override
    public Task convert(Comment source) {
        final Task task = new Task();
        task.setId(source.getId());
        task.setUrl(source.getUrl());
        task.setUrlApi(source.getUrlApi());
        task.setResponsible(source.getResponsible());
        task.setStatus(TaskStatus.OPEN);
        task.setPullRequestId(source.getPullRequestId());
        task.setBitbucketVersion(source.getBitbucketVersion());
        task.setCreateDate(source.getCreateDate());
        task.setDescription(source.getMessage());
        task.setAuthor(source.getAuthor());
        task.setAnswers(source.getAnswers());
        return task;
    }

}
