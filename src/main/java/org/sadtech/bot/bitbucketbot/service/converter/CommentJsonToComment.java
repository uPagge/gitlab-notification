package org.sadtech.bot.bitbucketbot.service.converter;

import org.sadtech.bot.bitbucketbot.domain.entity.Comment;
import org.sadtech.bot.bitbucketbot.dto.bitbucket.CommentJson;
import org.sadtech.bot.bitbucketbot.dto.bitbucket.Severity;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class CommentJsonToComment implements Converter<CommentJson, Comment> {

    @Override
    public Comment convert(CommentJson source) {
        final Comment comment = new Comment();
        comment.setId(source.getId());
        comment.setCreateDate(source.getCreatedDate());
        comment.setAuthor(source.getAuthor().getName());
        comment.setPullRequestId(source.getCustomPullRequestId());
        comment.setMessage(source.getText());
        comment.setUrlApi(source.getCustomCommentApiUrl());
        comment.setBitbucketVersion(source.getVersion());
        comment.setAnswers(
                source.getComments().stream()
                        .filter(json -> Severity.NORMAL.equals(json.getSeverity()))
                        .map(CommentJson::getId)
                        .collect(Collectors.toSet())
        );
        return comment;

    }

}
