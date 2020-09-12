package org.sadtech.bot.bitbucketbot.service.converter;

import org.sadtech.bot.bitbucketbot.domain.entity.Comment;
import org.sadtech.bot.bitbucketbot.dto.bitbucket.CommentJson;
import org.sadtech.bot.bitbucketbot.dto.bitbucket.Severity;
import org.sadtech.bot.bitbucketbot.service.executor.ResultScan;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class ResultScanToComment implements Converter<ResultScan, Comment> {

    @Override
    public Comment convert(ResultScan resultScan) {
        final CommentJson commentJson = resultScan.getCommentJson();
        final Comment comment = new Comment();
        comment.setId(commentJson.getId());
        comment.setCreateDate(commentJson.getCreatedDate());
        comment.setAuthor(commentJson.getAuthor().getName());
        comment.setPullRequestId(resultScan.getPullRequestId());
        comment.setMessage(commentJson.getText());
        comment.setUrlApi(resultScan.getCommentApiUrl());
        comment.setBitbucketVersion(commentJson.getVersion());
        comment.setAnswers(
                commentJson.getComments().stream()
                        .filter(json -> Severity.NORMAL.equals(json.getSeverity()))
                        .map(CommentJson::getId)
                        .collect(Collectors.toSet())
        );
        return comment;

    }

}
