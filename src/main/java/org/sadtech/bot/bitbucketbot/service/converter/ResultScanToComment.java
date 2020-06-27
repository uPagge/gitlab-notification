package org.sadtech.bot.bitbucketbot.service.converter;

import org.sadtech.bot.bitbucketbot.domain.entity.Comment;
import org.sadtech.bot.bitbucketbot.domain.entity.Person;
import org.sadtech.bot.bitbucketbot.domain.entity.PullRequest;
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
        comment.setCreateDate(commentJson.getCreatedDate());
        comment.setAuthor(commentJson.getAuthor().getName());
        comment.setPullRequestId(getPullRequest(resultScan.getPullRequestId()));
        comment.setMessage(commentJson.getText());
        comment.setUrl(resultScan.getUrlComment());
        comment.setBitbucketVersion(commentJson.getVersion());
        comment.setAnswers(
                commentJson.getComments().stream()
                        .filter(json -> Severity.NORMAL.equals(json.getSeverity()))
                        .map(CommentJson::getId)
                        .collect(Collectors.toSet())
        );
        return comment;

    }

    private PullRequest getPullRequest(Long pullRequestId) {
        final PullRequest pullRequest = new PullRequest();
        pullRequest.setId(pullRequestId);
        return pullRequest;
    }

    private Person getAuthor(String name) {
        final Person user = new Person();
        user.setLogin(name);
        return user;
    }

}
