package org.sadtech.bot.vcs.bitbucket.app.service.converter;

import org.sadtech.bot.vcs.bitbucket.app.exception.ConvertException;
import org.sadtech.bot.vcs.bitbucket.sdk.domain.CommentJson;
import org.sadtech.bot.vcs.bitbucket.sdk.domain.CommentState;
import org.sadtech.bot.vcs.bitbucket.sdk.domain.Severity;
import org.sadtech.bot.vcs.core.domain.TaskStatus;
import org.sadtech.bot.vcs.core.domain.entity.Task;
import org.sadtech.bot.vcs.core.utils.StringUtils;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class CommentJsonToTaskConvert implements Converter<CommentJson, Task> {

    @Override
    public Task convert(CommentJson source) {
        final Task task = new Task();
        task.setId(source.getId());
        task.setAuthor(source.getAuthor().getName());
        task.setDescription(StringUtils.cutOff(source.getText(), 490));
        task.setCreateDate(source.getCreatedDate());
        task.setBitbucketVersion(source.getVersion());
        task.setPullRequestId(source.getCustomPullRequestId());
        task.setStatus(convertState(source.getState()));
        task.setUrlApi(source.getCustomCommentApiUrl());
        task.setAnswers(
                source.getComments().stream()
                        .filter(json -> Severity.NORMAL.equals(json.getSeverity()))
                        .map(CommentJson::getId)
                        .collect(Collectors.toSet())
        );
        return task;
    }

    private TaskStatus convertState(CommentState state) {
        switch (state) {
            case RESOLVED:
                return TaskStatus.RESOLVED;
            case OPEN:
                return TaskStatus.OPEN;
            default:
                throw new ConvertException("Неподдерживаемый тип задачи");
        }
    }

}
