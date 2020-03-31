package com.tsc.bitbucketbot.scheduler;

import com.tsc.bitbucketbot.config.BitbucketConfig;
import com.tsc.bitbucketbot.domain.MessageSend;
import com.tsc.bitbucketbot.domain.PullRequestStatus;
import com.tsc.bitbucketbot.domain.ReviewerStatus;
import com.tsc.bitbucketbot.domain.entity.PullRequest;
import com.tsc.bitbucketbot.domain.entity.Reviewer;
import com.tsc.bitbucketbot.domain.entity.User;
import com.tsc.bitbucketbot.domain.util.ReviewerChange;
import com.tsc.bitbucketbot.dto.bitbucket.PullRequestJson;
import com.tsc.bitbucketbot.dto.bitbucket.sheet.PullRequestSheetJson;
import com.tsc.bitbucketbot.service.MessageSendService;
import com.tsc.bitbucketbot.service.PullRequestsService;
import com.tsc.bitbucketbot.service.UserService;
import com.tsc.bitbucketbot.service.Utils;
import com.tsc.bitbucketbot.service.converter.PullRequestJsonConverter;
import com.tsc.bitbucketbot.utils.Message;
import com.tsc.bitbucketbot.utils.Pair;
import com.tsc.bitbucketbot.utils.Smile;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.ConversionService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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

    private final PullRequestsService pullRequestsService;
    private final UserService userService;
    private final MessageSendService messageSendService;
    private final ConversionService conversionService;
    private final BitbucketConfig bitbucketConfig;

    @Scheduled(fixedRate = 30000)
    public void checkOldPullRequest() {
        Set<Long> existsId = pullRequestsService.getAllId();
        Set<Long> openId = checkOpenPullRequest();
        checkClosePullRequest();
        existsId.removeAll(openId);
        if (!existsId.isEmpty()) {
            pullRequestsService.deleteAll(existsId);
        }
    }

    private void checkClosePullRequest() {
        final List<User> users = userService.getAllRegister();
        for (User user : users) {
            Optional<PullRequestSheetJson> sheetJson = Utils.urlToJson(bitbucketConfig.getUrlPullRequestClose(), user.getToken(), PullRequestSheetJson.class);
            while (sheetJson.isPresent() && sheetJson.get().getValues() != null && !sheetJson.get().getValues().isEmpty()) {
                final PullRequestSheetJson bitbucketSheet = sheetJson.get();
                final List<PullRequestJson> jsonsPr = bitbucketSheet.getValues().stream()
                        .filter(
                                jsonPr -> pullRequestsService.existsByBitbucketIdAndReposId(
                                        jsonPr.getId(),
                                        jsonPr.getFromRef().getRepository().getId()
                                )
                        )
                        .collect(Collectors.toList());
                final Set<Long> idPr = jsonsPr.stream()
                        .map(
                                jsonPr -> pullRequestsService.getIdByBitbucketIdAndReposId(
                                        jsonPr.getId(),
                                        jsonPr.getFromRef().getRepository().getId()
                                )
                        )
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .collect(Collectors.toSet());
                for (PullRequestJson jsonPr : jsonsPr) {
                    final Optional<User> optUser = userService.getByLogin(jsonPr.getAuthor().getUser().getName());
                    if (optUser.isPresent()) {
                        final User author = optUser.get();
                        final Long telegramId = author.getTelegramId();
                        if (telegramId != null) {
                            final PullRequestStatus statusPr = PullRequestJsonConverter.convertPullRequestStatus(jsonPr.getState());
                            final String message = Message.statusPullRequest(jsonPr.getTitle(), jsonPr.getLinks().getSelf().get(0).getHref(), PullRequestStatus.OPEN, statusPr);
                            messageSendService.add(MessageSend.builder().telegramId(telegramId).message(message).build());
                        }
                    }
                }

                pullRequestsService.deleteAll(idPr);

                if (bitbucketSheet.getNextPageStart() != null) {
                    sheetJson = Utils.urlToJson(bitbucketConfig.getUrlPullRequestClose() + bitbucketSheet.getNextPageStart(), bitbucketConfig.getToken(), PullRequestSheetJson.class);
                } else {
                    break;
                }
            }
        }
    }

    private Set<Long> checkOpenPullRequest() {
        final List<User> users = userService.getAllRegister();
        final Set<Long> ids = new HashSet<>();
        for (User user : users) {
            Optional<PullRequestSheetJson> sheetJson = Utils.urlToJson(bitbucketConfig.getUrlPullRequestOpen(), user.getToken(), PullRequestSheetJson.class);
            while (sheetJson.isPresent() && sheetJson.get().getValues() != null && !sheetJson.get().getValues().isEmpty()) {
                final PullRequestSheetJson jsonSheet = sheetJson.get();
                final Map<Long, PullRequest> existsJsonPr = jsonSheet.getValues().stream()
                        .filter(Objects::nonNull)
                        .map(pullRequestJson -> conversionService.convert(pullRequestJson, PullRequest.class))
                        .peek(pullRequest -> pullRequestsService.getIdByBitbucketIdAndReposId(pullRequest.getBitbucketId(), pullRequest.getRepositoryId()).ifPresent(pullRequest::setId))
                        .filter(pullRequest -> pullRequest.getId() != null)
                        .collect(Collectors.toMap(PullRequest::getId, pullRequest -> pullRequest));
                final Set<PullRequest> pullRequests = pullRequestsService.getAllById(existsJsonPr.keySet());
                if (!existsJsonPr.isEmpty() && !pullRequests.isEmpty()) {
                    processingUpdate(existsJsonPr, pullRequests);
                    ids.addAll(
                            pullRequestsService.updateAll(existsJsonPr.values()).stream()
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

    @NonNull
    private void processingUpdate(Map<Long, PullRequest> newPullRequests, Set<PullRequest> pullRequests) {
        for (PullRequest pullRequest : pullRequests) {
            PullRequest newPullRequest = newPullRequests.get(pullRequest.getId());
            processingAuthor(pullRequest, newPullRequest);
            processingReviewer(pullRequest, newPullRequest);
        }
    }

    private void processingReviewer(PullRequest pullRequest, PullRequest newPullRequest) {
        final Set<String> logins = newPullRequest.getReviewers().stream()
                .map(Reviewer::getUser)
                .collect(Collectors.toSet());
        if (!logins.isEmpty()) {
            Optional<String> optMessage = changeVersionPr(pullRequest, newPullRequest);
            if (optMessage.isPresent()) {
                final String message = optMessage.get();
                userService.getAllTelegramIdByLogin(logins).forEach(
                        telegramId -> messageSendService.add(
                                MessageSend.builder()
                                        .telegramId(telegramId)
                                        .message(message)
                                        .build()
                        )
                );
            }
        }
    }

    @NonNull
    private void processingAuthor(PullRequest pullRequest, PullRequest newPullRequest) {
        final User author = pullRequest.getAuthor();
        StringBuilder builderMessage = new StringBuilder();
        if (author.getTelegramId() != null) {
            changeStatusPR(pullRequest, newPullRequest).ifPresent(builderMessage::append);
            changeReviewersPR(pullRequest, newPullRequest).ifPresent(builderMessage::append);
            final String message = builderMessage.toString();
            if (!Smile.Constants.EMPTY.equalsIgnoreCase(message)) {
                messageSendService.add(
                        MessageSend.builder()
                                .message(message)
                                .telegramId(author.getTelegramId())
                                .build()
                );
            }
        }
    }

    @NonNull
    private Optional<String> changeVersionPr(PullRequest pullRequest, PullRequest newPullRequest) {
        LocalDateTime oldDate = pullRequest.getUpdateDate();
        LocalDateTime newDate = newPullRequest.getUpdateDate();
        if (!oldDate.isEqual(newDate)) {
            return Optional.of(
                    Message.updatePullRequest(
                            newPullRequest.getName(),
                            newPullRequest.getUrl(),
                            newPullRequest.getAuthor().getLogin()
                    )
            );
        }
        return Optional.empty();
    }

    @NonNull
    private Optional<String> changeReviewersPR(PullRequest pullRequest, PullRequest newPullRequest) {
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
        return Message.statusReviewers(pullRequest, reviewerChanges);
    }


    @NonNull
    private Optional<String> changeStatusPR(PullRequest pullRequest, PullRequest newPullRequest) {
        final PullRequestStatus oldStatus = pullRequest.getStatus();
        final PullRequestStatus newStatus = newPullRequest.getStatus();
        if (!oldStatus.equals(newStatus)) {
            return Optional.of(Message.statusPullRequest(pullRequest.getName(), pullRequest.getUrl(), oldStatus, newStatus));
        }
        return Optional.empty();
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
            newPullRequests.forEach(
                    pullRequest -> pullRequest.getReviewers().stream()
                            .map(reviewer -> userService.getByLogin(reviewer.getUser()))
                            .filter(Optional::isPresent)
                            .map(Optional::get)
                            .filter(user -> user.getTelegramId() != null)
                            .forEach(user -> messageSendService.add(
                                    MessageSend.builder()
                                            .telegramId(user.getTelegramId())
                                            .message(Message.newPullRequest(pullRequest))
                                            .build()
                            ))
            );
        }
    }

}
