package com.tsc.bitbucketbot.service.converter;

import com.tsc.bitbucketbot.bitbucket.PullRequestJson;
import com.tsc.bitbucketbot.bitbucket.PullRequestState;
import com.tsc.bitbucketbot.bitbucket.UserJson;
import com.tsc.bitbucketbot.domain.PullRequestStatus;
import com.tsc.bitbucketbot.domain.entity.PullRequest;
import com.tsc.bitbucketbot.domain.entity.User;
import com.tsc.bitbucketbot.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

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
                .id(json.getId())
                .author(this.convertUser(json.getAuthor().getUser()))
                .name(json.getTitle())
                .url(json.getLinks().getSelf().get(0).getHref())
                .status(convertPullRequestStatus(json.getState()))
                .build();
    }

    private User convertUser(UserJson userJson) {
        return userService.getByLogin(userJson.getName()).orElse(userJsonConverter.convert(userJson));
    }

    private PullRequestStatus convertPullRequestStatus(PullRequestState state) {
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

//    private List<Reviewer> convertReviewers(PullRequest pullRequest, List<UserDecisionJson> jsonReviewers) {
//        return jsonReviewers.stream()
//                .map(
//                        jsonReviewer -> {
//                            final Reviewer reviewer = new Reviewer();
//                            reviewer.setReviewerKey(new ReviewerKey(convertUser(jsonReviewer.getUser()).getLogin()));
//                            reviewer.setStatus(convertStatusReviewer(jsonReviewer.getStatus()));
//                            return reviewer;
//                        }
//                )
//                .collect(Collectors.toList());
//    }



}
