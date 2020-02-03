package com.tsc.bitbucketbot.scheduler;

import com.tsc.bitbucketbot.bitbucket.PullRequestJson;
import com.tsc.bitbucketbot.bitbucket.UserDecisionJson;
import com.tsc.bitbucketbot.bitbucket.UserPullRequestStatus;
import com.tsc.bitbucketbot.bitbucket.sheet.PullRequestSheetJson;
import com.tsc.bitbucketbot.config.BitbucketConfig;
import com.tsc.bitbucketbot.domain.ReviewerStatus;
import com.tsc.bitbucketbot.domain.entity.PullRequest;
import com.tsc.bitbucketbot.domain.entity.Reviewer;
import com.tsc.bitbucketbot.domain.entity.User;
import com.tsc.bitbucketbot.service.PullRequestsService;
import com.tsc.bitbucketbot.service.UserService;
import com.tsc.bitbucketbot.service.Utils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.sadtech.social.core.domain.BoxAnswer;
import org.sadtech.social.core.service.sender.Sending;
import org.springframework.core.convert.ConversionService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * TODO: Добавить описание класса.
 *
 * @author upagge [30.01.2020]
 */
@Service
@RequiredArgsConstructor
public class SchedulerPullRequest {

    private static final String URL = "http://192.168.236.164:7990/rest/api/1.0/dashboard/pull-requests?limit=50&state=OPEN";
    private final BitbucketConfig bitbucketConfig;
    private final PullRequestsService pullRequestsService;
    private final UserService userService;
    private final ConversionService conversionService;
    private final Sending sending;

    @Scheduled(fixedRate = 15000)
    public void checkNewPullRequest() {
        final List<User> users = userService.getAllRegistered();
        for (User user : users) {
            Optional<PullRequestSheetJson> sheetJson = Utils.urlToJson(URL, user.getToken(), PullRequestSheetJson.class);
            while (sheetJson.isPresent() && sheetJson.get().getValues() != null && !sheetJson.get().getValues().isEmpty()) {
                final PullRequestSheetJson pullRequestBitbucketSheet = sheetJson.get();
                final Set<Long> pullRequestBitbucketId = pullRequestBitbucketSheet.getValues().stream()
                        .map(PullRequestJson::getId)
                        .collect(Collectors.toSet());
                Set<Long> existsId = pullRequestsService.existsAllById(pullRequestBitbucketId);
                final Set<PullRequestJson> newPullRequestBitbucket = pullRequestBitbucketSheet.getValues().stream()
                        .filter(pullRequestJson -> !existsId.contains(pullRequestJson.getId()))
                        .collect(Collectors.toSet());
                pullRequestsService.addAll(
                        newPullRequestBitbucket.stream()
                                .map(pullRequestJson -> conversionService.convert(pullRequestJson, PullRequest.class))
                                .collect(Collectors.toSet())
                );
                final List<PullRequest> newPullRequests = new ArrayList<>();
                for (PullRequestJson pullRequestJson : newPullRequestBitbucket) {
                    final List<Reviewer> reviewers = pullRequestJson.getReviewers().stream()
                            .map(reviewer -> testConvert(pullRequestJson, reviewer))
                            .collect(Collectors.toList());
                    pullRequestsService.addReviewer(pullRequestJson.getId(), reviewers).ifPresent(newPullRequests::add);
                }
                sendNotification(newPullRequests);
                if (pullRequestBitbucketSheet.getNextPageStart() != null) {
                    sheetJson = Utils.urlToJson(URL + pullRequestBitbucketSheet.getNextPageStart(), bitbucketConfig.getToken(), PullRequestSheetJson.class);
                } else {
                    break;
                }
            }
        }
    }

    private Reviewer testConvert(PullRequestJson pullRequestJson, UserDecisionJson reviewer) {
        final Reviewer newReviewer = new Reviewer();
        newReviewer.setPullRequestId(pullRequestJson.getId());
        newReviewer.setUser(reviewer.getUser().getName());
        newReviewer.setStatus(convertStatusReviewer(reviewer.getStatus()));
        return newReviewer;
    }

    private ReviewerStatus convertStatusReviewer(UserPullRequestStatus status) {
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

    private void sendNotification(@NonNull List<PullRequest> newPullRequests) {
        if (!newPullRequests.isEmpty()) {
            Map<Long, StringBuilder> map = new HashMap<>();
            newPullRequests.forEach(
                    pullRequest -> pullRequest.getReviewers().forEach(
                            reviewer -> test(pullRequest, reviewer, map)
                    )
            );
            map.forEach((key, value) -> sending.send(key, BoxAnswer.of(value.toString())));
        }
    }

    private void test(PullRequest pullRequest, Reviewer reviewer, Map<Long, StringBuilder> map) {
        userService.getByLogin(reviewer.getUser()).ifPresent(
                user -> {
                    final Long telegramId = user.getTelegramId();
                    if (telegramId != null) {
                        if (!map.containsKey(telegramId)) {
                            map.put(telegramId, new StringBuilder("У вас есть новые ПР:\n\n"));
                        }
                        map.get(telegramId).append("*").append(pullRequest.getName()).append("*\n").append("Автор: ").append(pullRequest.getAuthor().getName()).append("\nСсылка: ").append(pullRequest.getUrl()).append("\n-- -- -- -- --\n\n");
                    }
                }
        );
    }

}
