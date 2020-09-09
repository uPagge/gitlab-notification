package org.sadtech.bot.bitbucketbot.service.converter;

import lombok.RequiredArgsConstructor;
import org.sadtech.bot.bitbucketbot.domain.PullRequestStatus;
import org.sadtech.bot.bitbucketbot.domain.ReviewerStatus;
import org.sadtech.bot.bitbucketbot.domain.entity.PullRequest;
import org.sadtech.bot.bitbucketbot.domain.entity.Reviewer;
import org.sadtech.bot.bitbucketbot.dto.bitbucket.Outcome;
import org.sadtech.bot.bitbucketbot.dto.bitbucket.Properties;
import org.sadtech.bot.bitbucketbot.dto.bitbucket.PullRequestJson;
import org.sadtech.bot.bitbucketbot.dto.bitbucket.PullRequestState;
import org.sadtech.bot.bitbucketbot.dto.bitbucket.UserDecisionJson;
import org.sadtech.bot.bitbucketbot.dto.bitbucket.UserPullRequestStatus;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class PullRequestJsonConverter implements Converter<PullRequestJson, PullRequest> {

    @Override
    public PullRequest convert(PullRequestJson json) {

        final PullRequest pullRequest = new PullRequest();
        pullRequest.setBitbucketId(json.getId());
        pullRequest.setCreateDate(json.getCreatedDate());
        pullRequest.setUpdateDate(json.getUpdatedDate());
        pullRequest.setConflict(convertConflict(json.getProperties()));
        pullRequest.setDescription(convertDescription(json.getDescription()));
        pullRequest.setAuthorLogin(json.getAuthor().getUser().getName());
        pullRequest.setTitle(json.getTitle());
        pullRequest.setUrl(json.getLinks().getSelf().get(0).getHref());
        pullRequest.setStatus(convertPullRequestStatus(json.getState()));
        pullRequest.setProjectKey(json.getFromRef().getRepository().getProject().getKey());
        pullRequest.setRepositorySlug(json.getFromRef().getRepository().getSlug());
        pullRequest.setReviewers(convertReviewers(json.getReviewers()));
        pullRequest.setBitbucketVersion(json.getVersion());
        pullRequest.setRepositoryId(json.getFromRef().getRepository().getId());
        return pullRequest;
    }

    private boolean convertConflict(Properties properties) {
        return properties != null
                && properties.getMergeResult() != null
                && properties.getMergeResult().getOutcome() != null
                && Outcome.CONFLICTED.equals(properties.getMergeResult().getOutcome());
    }

    private String convertDescription(String description) {
        if (description != null) {
            return description.length() > 180 ? description.substring(0, 180) + "..." : description;
        }
        return null;
    }

    public static PullRequestStatus convertPullRequestStatus(PullRequestState state) {
        switch (state) {
            case OPEN:
                return PullRequestStatus.OPEN;
            case MERGED:
                return PullRequestStatus.MERGED;
            case DECLINED:
                return PullRequestStatus.DECLINED;
        }
        return null;
    }

    private List<Reviewer> convertReviewers(List<UserDecisionJson> jsonReviewers) {
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

    private static ReviewerStatus convertStatusReviewer(UserPullRequestStatus status) {
        switch (status) {
            case APPROVED:
                return ReviewerStatus.APPROVED;
            case NEEDS_WORK:
                return ReviewerStatus.UNAPPROVED;
            case UNAPPROVED:
                return ReviewerStatus.NEEDS_WORK;
        }
        return null;
    }

}
