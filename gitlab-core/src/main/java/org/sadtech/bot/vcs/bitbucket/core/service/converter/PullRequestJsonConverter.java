package org.sadtech.bot.vcs.bitbucket.core.service.converter;

import lombok.RequiredArgsConstructor;
import org.sadtech.bot.gitlab.context.domain.entity.PullRequest;
import org.sadtech.bot.gitlab.context.domain.entity.Reviewer;
import org.sadtech.bot.gitlab.core.utils.StringUtils;
import org.sadtech.bot.gitlab.sdk.domain.AuthorJson;
import org.sadtech.bot.gitlab.sdk.domain.Outcome;
import org.sadtech.bot.gitlab.sdk.domain.Properties;
import org.sadtech.bot.gitlab.sdk.domain.PullRequestJson;
import org.sadtech.bot.gitlab.sdk.domain.PullRequestState;
import org.sadtech.bot.gitlab.sdk.domain.UserPullRequestStatus;
import org.sadtech.bot.vsc.context.domain.PullRequestStatus;
import org.sadtech.bot.vsc.context.domain.ReviewerStatus;
import org.sadtech.haiti.context.exception.ConvertException;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class PullRequestJsonConverter implements Converter<PullRequestJson, PullRequest> {

    public static PullRequestStatus convertPullRequestStatus(PullRequestState state) {
        switch (state) {
            case OPENED:
                return PullRequestStatus.OPEN;
            case MERGED:
                return PullRequestStatus.MERGED;
            case DECLINED:
                return PullRequestStatus.DECLINED;
            default:
                throw new ConvertException("Неподдерживаемый тип ПР");
        }
    }

    private static ReviewerStatus convertStatusReviewer(UserPullRequestStatus status) {
        switch (status) {
            case APPROVED:
                return ReviewerStatus.APPROVED;
            case NEEDS_WORK:
                return ReviewerStatus.UNAPPROVED;
            case UNAPPROVED:
                return ReviewerStatus.NEEDS_WORK;
            default:
                throw new ConvertException("Неподдерживаемый статус ревьювера");
        }
    }

    @Override
    public PullRequest convert(PullRequestJson json) {

        final PullRequest pullRequest = new PullRequest();
        pullRequest.setBitbucketId(json.getId());
        pullRequest.setCreateDate(json.getCreatedDate());
        pullRequest.setUpdateDate(json.getUpdatedDate());
        pullRequest.setConflict(convertConflict(json.getProperties()));
        pullRequest.setDescription(StringUtils.cutOff(json.getDescription(), 180));
        pullRequest.setAuthorLogin(json.getAuthor().getUser().getName());
        pullRequest.setTitle(StringUtils.cutOff(json.getTitle(), 90));
        pullRequest.setUrl(json.getLinks().getSelf().get(0).getHref());
        pullRequest.setStatus(convertPullRequestStatus(json.getState()));
        pullRequest.setProjectKey(json.getFromRef().getRepository().getProject().getKey());
        pullRequest.setRepositorySlug(json.getFromRef().getRepository().getSlug());
        pullRequest.setReviewers(convertReviewers(json.getReviewers()));
        pullRequest.setBitbucketVersion(json.getVersion());
        pullRequest.setRepositoryId(json.getFromRef().getRepository().getId());
        pullRequest.setResolvedTaskCount(json.getProperties().getResolvedTaskCount());
        pullRequest.setCommentCount(json.getProperties().getCommentCount());
        pullRequest.setOpenTaskCount(json.getProperties().getOpenTaskCount());
        return pullRequest;
    }

    private boolean convertConflict(Properties properties) {
        return properties != null
                && properties.getMergeResult() != null
                && properties.getMergeResult().getOutcome() != null
                && Outcome.CONFLICTED.equals(properties.getMergeResult().getOutcome());
    }

    private List<Reviewer> convertReviewers(List<AuthorJson> jsonReviewers) {
        return jsonReviewers.stream()
                .map(
                        jsonReviewer -> {
                            final Reviewer reviewer = new Reviewer();
                            reviewer.setPersonLogin(jsonReviewer.getUser().getName());
                            reviewer.setStatus(convertStatusReviewer(jsonReviewer.getStatus()));
                            return reviewer;
                        }
                )
                .collect(Collectors.toList());
    }

}
