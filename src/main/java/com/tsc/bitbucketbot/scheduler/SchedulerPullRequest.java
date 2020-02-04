package com.tsc.bitbucketbot.scheduler;

import com.tsc.bitbucketbot.bitbucket.PullRequestJson;
import com.tsc.bitbucketbot.bitbucket.UserDecisionJson;
import com.tsc.bitbucketbot.bitbucket.sheet.PullRequestSheetJson;
import com.tsc.bitbucketbot.config.BitbucketConfig;
import com.tsc.bitbucketbot.domain.PullRequestStatus;
import com.tsc.bitbucketbot.domain.ReviewerStatus;
import com.tsc.bitbucketbot.domain.entity.PullRequest;
import com.tsc.bitbucketbot.domain.entity.Reviewer;
import com.tsc.bitbucketbot.domain.entity.User;
import com.tsc.bitbucketbot.service.PullRequestsService;
import com.tsc.bitbucketbot.service.UserService;
import com.tsc.bitbucketbot.service.Utils;
import com.tsc.bitbucketbot.service.converter.PullRequestJsonConverter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.sadtech.social.core.domain.BoxAnswer;
import org.sadtech.social.core.service.sender.Sending;
import org.springframework.core.convert.ConversionService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

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

    private static final String URL_NEW_PR = "http://192.168.236.164:7990/rest/api/1.0/dashboard/pull-requests?limit=150&state=OPEN";
    private static final String URL_OLD_PR = "http://192.168.236.164:7990/rest/api/1.0/dashboard/pull-requests?limit=150&closedSince=86400";
    private final BitbucketConfig bitbucketConfig;
    private final PullRequestsService pullRequestsService;
    private final UserService userService;
    private final ConversionService conversionService;
    private final Sending sending;

    @Scheduled(fixedRate = 15000)
    public void checkOldPullRequest() {
        final List<User> users = userService.getAllRegistered();
        for (User user : users) {
            Optional<PullRequestSheetJson> sheetJson = Utils.urlToJson(URL_OLD_PR, user.getToken(), PullRequestSheetJson.class);
            while (sheetJson.isPresent() && sheetJson.get().getValues() != null && !sheetJson.get().getValues().isEmpty()) {
                final PullRequestSheetJson pullRequestBitbucketSheet = sheetJson.get();
                Set<Long> existsId = pullRequestsService.existsById(
                        pullRequestBitbucketSheet.getValues().stream()
                                .map(PullRequestJson::getId)
                                .collect(Collectors.toSet())
                );
                final Map<Long, PullRequestJson> existsPullRequestBitbucket = pullRequestBitbucketSheet.getValues().stream()
                        .filter(pullRequestJson -> existsId.contains(pullRequestJson.getId()))
                        .collect(Collectors.toMap(PullRequestJson::getId, pullRequestJson -> pullRequestJson));
                final Set<PullRequest> pullRequests = pullRequestsService.getAllById(existsId);
                if (!existsPullRequestBitbucket.isEmpty() && !pullRequests.isEmpty()) {
                    processingUpdate(existsPullRequestBitbucket, pullRequests);
                }

                if (!existsPullRequestBitbucket.isEmpty()) {
                    pullRequestsService.updateAll(
                            existsPullRequestBitbucket.values().stream()
                                    .map(pullRequestBitbucket -> conversionService.convert(pullRequestBitbucket, PullRequest.class))
                                    .collect(Collectors.toList())
                    );
                }
                if (pullRequestBitbucketSheet.getNextPageStart() != null) {
                    sheetJson = Utils.urlToJson(URL_OLD_PR + pullRequestBitbucketSheet.getNextPageStart(), bitbucketConfig.getToken(), PullRequestSheetJson.class);
                } else {
                    break;
                }
            }
        }
    }

    private void processingUpdate(Map<Long, PullRequestJson> existsPullRequestBitbucket, Set<PullRequest> pullRequests) {
        for (PullRequest pullRequest : pullRequests) {
            final PullRequestJson pullRequestBitbucket = existsPullRequestBitbucket.get(pullRequest.getId());
            final User author = pullRequest.getAuthor();
            if (author.getTelegramId() != null) {
                sendStatusPR(pullRequest, pullRequestBitbucket);
                sendReviewersPR(pullRequest, pullRequestBitbucket);
            }
        }
    }

    private void sendReviewersPR(PullRequest pullRequest, PullRequestJson pullRequestBitbucket) {
        final Map<String, Reviewer> oldReviewers = pullRequest.getReviewers().stream().collect(Collectors.toMap(Reviewer::getUser, reviewer -> reviewer));
        final List<UserDecisionJson> newReviewers = pullRequestBitbucket.getReviewers();
        for (UserDecisionJson newReviewer : newReviewers) {
            if (oldReviewers.containsKey(newReviewer.getUser().getName())) {
                final Reviewer oldReviewer = oldReviewers.get(newReviewer.getUser().getName());
                final ReviewerStatus oldStatus = oldReviewer.getStatus();
                final ReviewerStatus newStatus = PullRequestJsonConverter.convertStatusReviewer(newReviewer.getStatus());
                boolean flag = false;
                StringBuilder stringBuilder = new StringBuilder("✏️ *Изменилось решение по вашему ПР*\n")
                        .append("[").append(pullRequest.getName()).append("](").append(pullRequest.getUrl()).append(")\n");
                if (!oldStatus.equals(newStatus)) {
                    flag = true;
                    stringBuilder.append("\uD83D\uDC68\u200D\uD83D\uDCBB️ ").append(oldReviewer.getUser()).append(" ")
                            .append(oldStatus).append(" -> ").append(newStatus).append("\n");
                }
                if (flag) {
                    sending.send(pullRequest.getAuthor().getTelegramId(), BoxAnswer.of(stringBuilder.toString()));
                }
            }
        }
    }

    private void sendStatusPR(PullRequest pullRequest, PullRequestJson pullRequestBitbucket) {
        final PullRequestStatus oldStatus = pullRequest.getStatus();
        final PullRequestStatus newStatus = PullRequestJsonConverter.convertPullRequestStatus(pullRequestBitbucket.getState());
        if (!oldStatus.equals(newStatus)) {
            StringBuilder stringBuilder = new StringBuilder("✏️ *Изменился статус вашего ПР*\n")
                    .append("[").append(pullRequest.getName()).append("](").append(pullRequest.getUrl()).append(")\n")
                    .append(oldStatus.name()).append(" -> ").append(newStatus.name())
                    .append("\n-- -- -- --\n")
                    .append("\uD83D\uDCCC: #pullRequest #change")
                    .append("\n\n");
            sending.send(pullRequest.getAuthor().getTelegramId(), BoxAnswer.of(stringBuilder.toString()));
        }
    }

    @Scheduled(fixedRate = 15000)
    public void checkNewPullRequest() {
        final List<User> users = userService.getAllRegistered();
        for (User user : users) {
            Optional<PullRequestSheetJson> sheetJson = Utils.urlToJson(URL_NEW_PR, user.getToken(), PullRequestSheetJson.class);
            while (sheetJson.isPresent() && sheetJson.get().getValues() != null && !sheetJson.get().getValues().isEmpty()) {
                final PullRequestSheetJson pullRequestBitbucketSheet = sheetJson.get();
                final Set<Long> pullRequestBitbucketId = pullRequestBitbucketSheet.getValues().stream()
                        .map(PullRequestJson::getId)
                        .collect(Collectors.toSet());
                Set<Long> existsId = pullRequestsService.existsAllIdById(pullRequestBitbucketId);
                final Set<PullRequestJson> newPullRequestBitbucket = pullRequestBitbucketSheet.getValues().stream()
                        .filter(pullRequestJson -> !existsId.contains(pullRequestJson.getId()))
                        .collect(Collectors.toSet());
                final List<PullRequest> newPullRequests = pullRequestsService.addAll(
                        newPullRequestBitbucket.stream()
                                .map(pullRequestJson -> conversionService.convert(pullRequestJson, PullRequest.class))
                                .collect(Collectors.toSet())
                );
                sendNotification(newPullRequests);
                if (pullRequestBitbucketSheet.getNextPageStart() != null) {
                    sheetJson = Utils.urlToJson(URL_NEW_PR + pullRequestBitbucketSheet.getNextPageStart(), bitbucketConfig.getToken(), PullRequestSheetJson.class);
                } else {
                    break;
                }
            }
        }
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
                            map.put(telegramId, new StringBuilder());
                        }
                        map.get(telegramId).append("\uD83C\uDF89 *Новый Pull Request*\n")
                                .append("[").append(pullRequest.getName()).append("](").append(pullRequest.getUrl()).append(")\n")
                                .append("\uD83D\uDC68\u200D\uD83D\uDCBB️: ").append(pullRequest.getAuthor().getName())
                                .append("\n-- -- -- -- --\n")
                                .append("\uD83D\uDCCC: ").append("#").append(pullRequest.getAuthor().getLogin()).append(" #pullRequest")
                                .append("\n\n");
                    }
                }
        );
    }

}
