package com.tsc.bitbucketbot.service.converter;

import com.tsc.bitbucketbot.domain.PullRequestStatus;
import com.tsc.bitbucketbot.domain.ReviewerStatus;
import com.tsc.bitbucketbot.domain.entity.PullRequest;
import com.tsc.bitbucketbot.domain.entity.Reviewer;
import com.tsc.bitbucketbot.domain.entity.User;
import com.tsc.bitbucketbot.dto.bitbucket.PullRequestJson;
import com.tsc.bitbucketbot.dto.bitbucket.PullRequestState;
import com.tsc.bitbucketbot.dto.bitbucket.UserDecisionJson;
import com.tsc.bitbucketbot.dto.bitbucket.UserJson;
import com.tsc.bitbucketbot.dto.bitbucket.UserPullRequestStatus;
import com.tsc.bitbucketbot.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * TODO: Добавить описание класса.
 *
 * @author upagge [01.02.2020]
 */
@Component
@RequiredArgsConstructor
public class PullRequestJsonConverter implements Converter<PullRequestJson, PullRequest> {

    private final UserJsonConverter userJsonConverter;
    private final UserService userService;

    @Override
    public PullRequest convert(PullRequestJson json) {
        return PullRequest.builder()
                .bitbucketId(json.getId())
                .version(json.getVersion())
                .createDate(json.getCreatedDate())
                .updateDate(json.getUpdatedDate())
                .description(convertDescription(json.getDescription()))
                .repositoryId(json.getFromRef().getRepository().getId())
                .author(this.convertUser(json.getAuthor().getUser()))
                .name(json.getTitle())
                .url(json.getLinks().getSelf().get(0).getHref())
                .status(convertPullRequestStatus(json.getState()))
                .projectKey(json.getFromRef().getRepository().getProject().getKey())
                .repositorySlug(json.getFromRef().getRepository().getSlug())
                .reviewers(convertReviewers(json.getReviewers()))
                .build();
    }

    private String convertDescription(String description) {
        if (description != null) {
            return description.length() > 180 ? description.substring(0, 180) + "..." : description;
        }
        return null;
    }

    private User convertUser(UserJson userJson) {
        return userService.getByLogin(userJson.getName()).orElse(userJsonConverter.convert(userJson));
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
                            reviewer.setUser(jsonReviewer.getUser().getName());
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
