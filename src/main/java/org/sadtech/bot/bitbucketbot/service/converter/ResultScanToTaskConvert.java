package org.sadtech.bot.bitbucketbot.service.converter;

import org.sadtech.bot.bitbucketbot.domain.entity.Task;
import org.sadtech.bot.bitbucketbot.dto.bitbucket.CommentJson;
import org.sadtech.bot.bitbucketbot.service.executor.ResultScan;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class ResultScanToTaskConvert implements Converter<ResultScan, Task> {

    @Override
    public Task convert(ResultScan resultScan) {
        final CommentJson json = resultScan.getCommentJson();
        final Task task = new Task();
        task.setId(json.getId());
        task.setAuthor(json.getAuthor().getName());
        task.setDescription(json.getText());
        task.setCreateDate(json.getCreatedDate());
        task.setBitbucketVersion(json.getVersion());
        task.setPullRequestId(resultScan.getPullRequestId());
        return task;
    }

}
