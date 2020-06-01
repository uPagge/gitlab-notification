package org.sadtech.bot.bitbucketbot.scheduler;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sadtech.bot.bitbucketbot.config.BitbucketConfig;
import org.sadtech.bot.bitbucketbot.domain.IdAndStatusPr;
import org.sadtech.bot.bitbucketbot.domain.PullRequestStatus;
import org.sadtech.bot.bitbucketbot.domain.ReviewerStatus;
import org.sadtech.bot.bitbucketbot.domain.change.ConflictPrChange;
import org.sadtech.bot.bitbucketbot.domain.change.NewPrChange;
import org.sadtech.bot.bitbucketbot.domain.change.ReviewersPrChange;
import org.sadtech.bot.bitbucketbot.domain.change.StatusPrChange;
import org.sadtech.bot.bitbucketbot.domain.change.UpdatePrChange;
import org.sadtech.bot.bitbucketbot.domain.entity.PullRequest;
import org.sadtech.bot.bitbucketbot.domain.entity.Reviewer;
import org.sadtech.bot.bitbucketbot.domain.entity.User;
import org.sadtech.bot.bitbucketbot.domain.util.ReviewerChange;
import org.sadtech.bot.bitbucketbot.dto.bitbucket.PullRequestJson;
import org.sadtech.bot.bitbucketbot.dto.bitbucket.sheet.PullRequestSheetJson;
import org.sadtech.bot.bitbucketbot.service.ChangeService;
import org.sadtech.bot.bitbucketbot.service.PullRequestsService;
import org.sadtech.bot.bitbucketbot.service.UserService;
import org.sadtech.bot.bitbucketbot.service.Utils;
import org.sadtech.bot.bitbucketbot.utils.NonNullUtils;
import org.sadtech.bot.bitbucketbot.utils.Pair;
import org.springframework.core.convert.ConversionService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.sadtech.bot.bitbucketbot.domain.PullRequestStatus.DECLINED;
import static org.sadtech.bot.bitbucketbot.domain.PullRequestStatus.DELETE;
import static org.sadtech.bot.bitbucketbot.domain.PullRequestStatus.MERGED;
import static org.sadtech.bot.bitbucketbot.domain.PullRequestStatus.OPEN;

/**
 * @author upagge [30.01.2020]
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SchedulerPullRequest {

    private static final Set<PullRequestStatus> STATUSES = Stream.of(MERGED, OPEN, DECLINED).collect(Collectors.toSet());

    private final PullRequestsService pullRequestsService;
    private final UserService userService;
    private final ChangeService changeService;
    private final ConversionService conversionService;
    private final BitbucketConfig bitbucketConfig;

    @Scheduled(fixedRate = 30000)
    public void checkPullRequest() {
        final Set<Long> existsId = pullRequestsService.getAllId(STATUSES).stream()
                .map(IdAndStatusPr::getId)
                .collect(Collectors.toSet());
        final Set<Long> openId = checkOpenPullRequest();
        log.info("Открыты: " + Arrays.toString(openId.toArray()));
        final Set<Long> closeId = checkClosePullRequest();
        log.info("Закрыты: " + Arrays.toString(closeId.toArray()));
        final Set<Long> newNotExistsId = existsId.stream()
                .filter(id -> !openId.contains(id) && !closeId.contains(id))
                .collect(Collectors.toSet());
        log.info("Не найдены: " + Arrays.toString(newNotExistsId.toArray()));
        if (!newNotExistsId.isEmpty()) {
            updateDeletePr(newNotExistsId);
        }
    }

    private void updateDeletePr(@NonNull Set<Long> ids) {
        final Set<PullRequest> deletePr = pullRequestsService.getAllById(ids);
        deletePr.stream()
                .filter(pullRequest -> pullRequest.getAuthor().getTelegramId() != null)
                .forEach(pullRequest -> changeService.add(
                        StatusPrChange.builder()
                                .name(pullRequest.getName())
                                .url(pullRequest.getUrl())
                                .oldStatus(pullRequest.getStatus())
                                .newStatus(DELETE)
                                .telegramIds(Collections.singleton(pullRequest.getAuthor().getTelegramId()))
                                .build()
                ));
        pullRequestsService.updateAll(
                deletePr.stream()
                        .peek(pullRequest -> pullRequest.setStatus(PullRequestStatus.DELETE))
                        .collect(Collectors.toList())
        );
    }

    private Set<Long> checkClosePullRequest() {
        final List<User> users = userService.getAllRegister();
        final Set<Long> ids = new HashSet<>();
        for (User user : users) {
            Optional<PullRequestSheetJson> sheetJson = Utils.urlToJson(bitbucketConfig.getUrlPullRequestClose(), user.getToken(), PullRequestSheetJson.class);
            while (sheetJson.isPresent() && sheetJson.get().getValues() != null && !sheetJson.get().getValues().isEmpty()) {
                final PullRequestSheetJson bitbucketSheet = sheetJson.get();
                final Map<Long, PullRequest> existsPr = getExistsPr(bitbucketSheet.getValues());
                final Set<PullRequest> pullRequests = pullRequestsService.getAllById(existsPr.keySet());
                if (!existsPr.isEmpty() && !pullRequests.isEmpty()) {
                    processingUpdateClosePr(existsPr, pullRequests);
                    ids.addAll(
                            pullRequestsService.updateAll(existsPr.values()).stream()
                                    .map(PullRequest::getId)
                                    .collect(Collectors.toSet())
                    );
                }

                if (bitbucketSheet.getNextPageStart() != null) {
                    sheetJson = Utils.urlToJson(bitbucketConfig.getUrlPullRequestClose() + bitbucketSheet.getNextPageStart(), bitbucketConfig.getToken(), PullRequestSheetJson.class);
                } else {
                    break;
                }
            }
        }
        return ids;
    }

    private Set<Long> checkOpenPullRequest() {
        final List<User> users = userService.getAllRegister();
        final Set<Long> ids = new HashSet<>();
        for (User user : users) {
            Optional<PullRequestSheetJson> sheetJson = Utils.urlToJson(bitbucketConfig.getUrlPullRequestOpen(), user.getToken(), PullRequestSheetJson.class);
            while (sheetJson.isPresent() && sheetJson.get().getValues() != null && !sheetJson.get().getValues().isEmpty()) {
                final PullRequestSheetJson jsonSheet = sheetJson.get();
                final Map<Long, PullRequest> existsPr = getExistsPr(jsonSheet.getValues());
                final Set<PullRequest> pullRequests = pullRequestsService.getAllById(existsPr.keySet());
                if (!existsPr.isEmpty() && !pullRequests.isEmpty()) {
                    processingUpdateOpenPr(existsPr, pullRequests);
                    ids.addAll(
                            pullRequestsService.updateAll(existsPr.values()).stream()
                                    .map(PullRequest::getId)
                                    .collect(Collectors.toSet())
                    );
                }

                if (jsonSheet.getNextPageStart() != null) {
                    sheetJson = Utils.urlToJson(bitbucketConfig.getUrlPullRequestOpen() + jsonSheet.getNextPageStart(), bitbucketConfig.getToken(), PullRequestSheetJson.class);
                } else {
                    break;
                }
            }
        }
        return ids;
    }

    private Map<Long, PullRequest> getExistsPr(List<PullRequestJson> pullRequestJsons) {
        return pullRequestJsons.stream()
                .filter(Objects::nonNull)
                .map(pullRequestJson -> conversionService.convert(pullRequestJson, PullRequest.class))
                .peek(pullRequest -> pullRequestsService.getIdByBitbucketIdAndReposId(pullRequest.getBitbucketId(), pullRequest.getRepositoryId()).ifPresent(pullRequest::setId))
                .filter(pullRequest -> pullRequest.getId() != null)
                .collect(Collectors.toMap(PullRequest::getId, pullRequest -> pullRequest));
    }

    @NonNull
    private void processingUpdateOpenPr(Map<Long, PullRequest> newPullRequests, Set<PullRequest> pullRequests) {
        for (PullRequest pullRequest : pullRequests) {
            PullRequest newPullRequest = newPullRequests.get(pullRequest.getId());
            changeStatusPR(pullRequest, newPullRequest);
            changeReviewersPR(pullRequest, newPullRequest);
            processingReviewer(pullRequest, newPullRequest);
            conflictPr(pullRequest, newPullRequest);
        }
    }

    @NonNull
    private void processingUpdateClosePr(Map<Long, PullRequest> newPullRequests, Set<PullRequest> pullRequests) {
        for (PullRequest pullRequest : pullRequests) {
            PullRequest newPullRequest = newPullRequests.get(pullRequest.getId());
            changeStatusPR(pullRequest, newPullRequest);
        }
    }

    @NonNull
    private void conflictPr(PullRequest pullRequest, PullRequest newPullRequest) {
        if (newPullRequest.isConflict() && !pullRequest.isConflict()) {
            changeService.add(
                    ConflictPrChange.builder()
                            .name(pullRequest.getName())
                            .url(pullRequest.getUrl())
                            .telegramIds(NonNullUtils.telegramIdByUser(pullRequest.getAuthor()))
                            .build()
            );
        }
    }

    private void processingReviewer(PullRequest pullRequest, PullRequest newPullRequest) {
        if (isUpdatePr(pullRequest, newPullRequest)) {
            final Set<String> logins = newPullRequest.getReviewers().stream()
                    .map(Reviewer::getUser)
                    .collect(Collectors.toSet());
            final Set<Long> telegramIds = userService.getAllTelegramIdByLogin(logins);
            changeService.add(
                    UpdatePrChange.builder()
                            .name(newPullRequest.getName())
                            .url(newPullRequest.getUrl())
                            .author(newPullRequest.getAuthor().getLogin())
                            .telegramIds(telegramIds)
                            .build()
            );
        }
    }

    @NonNull
    private boolean isUpdatePr(PullRequest pullRequest, PullRequest newPullRequest) {
        LocalDateTime oldDate = pullRequest.getUpdateDate();
        LocalDateTime newDate = newPullRequest.getUpdateDate();
        return !oldDate.isEqual(newDate);
    }

    @NonNull
    private void changeReviewersPR(PullRequest pullRequest, PullRequest newPullRequest) {
        final Map<String, Reviewer> oldReviewers = pullRequest.getReviewers().stream()
                .collect(Collectors.toMap(Reviewer::getUser, reviewer -> reviewer));
        final Map<String, Reviewer> newReviewers = newPullRequest.getReviewers().stream()
                .collect(Collectors.toMap(Reviewer::getUser, reviewer -> reviewer));
        List<ReviewerChange> reviewerChanges = new ArrayList<>();
        for (Reviewer newReviewer : newReviewers.values()) {
            if (oldReviewers.containsKey(newReviewer.getUser())) {
                final Reviewer oldReviewer = oldReviewers.get(newReviewer.getUser());
                final ReviewerStatus oldStatus = oldReviewer.getStatus();
                final ReviewerStatus newStatus = newReviewer.getStatus();
                if (!oldStatus.equals(newStatus)) {
                    reviewerChanges.add(ReviewerChange.ofOld(oldReviewer.getUser(), oldStatus, newStatus));
                }
            } else {
                reviewerChanges.add(ReviewerChange.ofNew(newReviewer.getUser(), newReviewer.getStatus()));
            }
        }
        final Set<String> oldLogins = oldReviewers.keySet();
        oldLogins.removeAll(newReviewers.keySet());
        oldLogins.forEach(login -> reviewerChanges.add(ReviewerChange.ofDeleted(login)));
        if (!reviewerChanges.isEmpty()) {
            changeService.add(
                    ReviewersPrChange.builder()
                            .name(pullRequest.getName())
                            .url(pullRequest.getUrl())
                            .reviewerChanges(reviewerChanges)
                            .telegramIds(NonNullUtils.telegramIdByUser(pullRequest.getAuthor()))
                            .build()
            );
        }
    }


    @NonNull
    private void changeStatusPR(PullRequest pullRequest, PullRequest newPullRequest) {
        final PullRequestStatus oldStatus = pullRequest.getStatus();
        final PullRequestStatus newStatus = newPullRequest.getStatus();
        if (!oldStatus.equals(newStatus)) {
            changeService.add(
                    StatusPrChange.builder()
                            .name(newPullRequest.getName())
                            .url(newPullRequest.getUrl())
                            .oldStatus(oldStatus)
                            .newStatus(newStatus)
                            .telegramIds(NonNullUtils.telegramIdByUser(newPullRequest.getAuthor()))
                            .build()
            );
        }
    }

    @Scheduled(fixedRate = 30000)
    public void checkNewPullRequest() {
        final List<User> users = userService.getAllRegister();
        for (User user : users) {
            Optional<PullRequestSheetJson> sheetJson = Utils.urlToJson(bitbucketConfig.getUrlPullRequestOpen(), user.getToken(), PullRequestSheetJson.class);
            while (sheetJson.isPresent() && sheetJson.get().getValues() != null && !sheetJson.get().getValues().isEmpty()) {
                final PullRequestSheetJson pullRequestBitbucketSheet = sheetJson.get();
                final List<PullRequest> newPullRequest = pullRequestBitbucketSheet.getValues().stream()
                        .collect(Collectors.toMap(pullRequestJson -> new Pair<>(pullRequestJson.getId(), pullRequestJson.getFromRef().getRepository().getId()), pullRequestJson -> pullRequestJson))
                        .entrySet()
                        .stream()
                        .filter(test -> !pullRequestsService.existsByBitbucketIdAndReposId(test.getKey().getKey(), test.getKey().getValue()))
                        .map(test -> conversionService.convert(test.getValue(), PullRequest.class))
                        .collect(Collectors.toList());
                final List<PullRequest> newPullRequests = pullRequestsService.addAll(newPullRequest);
                sendNotificationNewPullRequest(newPullRequests);
                if (pullRequestBitbucketSheet.getNextPageStart() != null) {
                    sheetJson = Utils.urlToJson(bitbucketConfig.getUrlPullRequestOpen() + pullRequestBitbucketSheet.getNextPageStart(), bitbucketConfig.getToken(), PullRequestSheetJson.class);
                } else {
                    break;
                }
            }
        }
    }

    private void sendNotificationNewPullRequest(@NonNull List<PullRequest> newPullRequests) {
        if (!newPullRequests.isEmpty()) {
            for (PullRequest newPullRequest : newPullRequests) {
                final Set<Long> reviewerTelegramIds = userService.getAllTelegramIdByLogin(newPullRequest.getReviewers().stream()
                        .map(Reviewer::getUser)
                        .collect(Collectors.toSet()));
                changeService.add(
                        NewPrChange.builder()
                                .name(newPullRequest.getName())
                                .url(newPullRequest.getUrl())
                                .description(newPullRequest.getDescription())
                                .author(newPullRequest.getAuthor().getLogin())
                                .telegramIds(reviewerTelegramIds)
                                .build()
                );
            }
        }
    }

}
