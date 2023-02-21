package dev.struchkov.bot.gitlab.core.service.impl;

import dev.struchkov.bot.gitlab.context.domain.AssigneeChanged;
import dev.struchkov.bot.gitlab.context.domain.ExistContainer;
import dev.struchkov.bot.gitlab.context.domain.IdAndStatusPr;
import dev.struchkov.bot.gitlab.context.domain.MergeRequestState;
import dev.struchkov.bot.gitlab.context.domain.PersonInformation;
import dev.struchkov.bot.gitlab.context.domain.ReviewerChanged;
import dev.struchkov.bot.gitlab.context.domain.entity.Discussion;
import dev.struchkov.bot.gitlab.context.domain.entity.MergeRequest;
import dev.struchkov.bot.gitlab.context.domain.entity.MergeRequestForDiscussion;
import dev.struchkov.bot.gitlab.context.domain.entity.Person;
import dev.struchkov.bot.gitlab.context.domain.entity.Project;
import dev.struchkov.bot.gitlab.context.domain.notify.mergerequest.ConflictMrNotify;
import dev.struchkov.bot.gitlab.context.domain.notify.mergerequest.ConflictResolveMrNotify;
import dev.struchkov.bot.gitlab.context.domain.notify.mergerequest.NewMrForAssignee;
import dev.struchkov.bot.gitlab.context.domain.notify.mergerequest.NewMrForReview;
import dev.struchkov.bot.gitlab.context.domain.notify.mergerequest.StatusMrNotify;
import dev.struchkov.bot.gitlab.context.domain.notify.mergerequest.UpdateMrNotify;
import dev.struchkov.bot.gitlab.context.repository.MergeRequestRepository;
import dev.struchkov.bot.gitlab.context.service.DiscussionService;
import dev.struchkov.bot.gitlab.context.service.MergeRequestsService;
import dev.struchkov.bot.gitlab.context.service.NotifyService;
import dev.struchkov.bot.gitlab.context.service.ProjectService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static dev.struchkov.bot.gitlab.context.domain.MergeRequestState.CLOSED;
import static dev.struchkov.bot.gitlab.context.domain.MergeRequestState.MERGED;
import static dev.struchkov.haiti.context.exception.NotFoundException.notFoundException;
import static dev.struchkov.haiti.utils.Checker.checkNotEmpty;
import static dev.struchkov.haiti.utils.Checker.checkNotNull;
import static java.lang.Boolean.TRUE;

@Slf4j
@Service
@RequiredArgsConstructor
public class MergeRequestsServiceImpl implements MergeRequestsService {

    public static final Set<MergeRequestState> DELETE_STATES = Set.of(MERGED, CLOSED);

    private final MergeRequestRepository repository;

    private final NotifyService notifyService;
    private final ProjectService projectService;
    private final DiscussionService discussionService;

    private final PersonInformation personInformation;

    @Override
    @Transactional
    public MergeRequest create(@NonNull MergeRequest mergeRequest) {
        final boolean botUserReviewer = isBotUserReviewer(mergeRequest);
        final boolean botUserAssignee = isBotUserAssigneeAndNotAuthor(mergeRequest);

        mergeRequest.setNotification(botUserReviewer || botUserAssignee);
        mergeRequest.setUserAssignee(botUserAssignee);
        mergeRequest.setUserReviewer(botUserReviewer);

        final MergeRequest savedMergeRequest = repository.save(mergeRequest);

        if (botUserReviewer || botUserAssignee) {
            if (!mergeRequest.isConflict()) {
                final String projectName = projectService.getByIdOrThrow(savedMergeRequest.getProjectId()).getName();
                if (botUserReviewer) sendNotifyNewMrReview(savedMergeRequest, projectName);
                if (botUserAssignee) sendNotifyNewAssignee(mergeRequest, projectName, null);
            }
        }

        return savedMergeRequest;
    }

    private boolean isBotUserAssigneeAndNotAuthor(MergeRequest mergeRequest) {
        final Long gitlabUserId = personInformation.getId();
        final Person assignee = mergeRequest.getAssignee();
        final Person author = mergeRequest.getAuthor();

        if (checkNotNull(assignee)) {
            if (gitlabUserId.equals(assignee.getId()) && !isAuthorSameAssignee(author, assignee)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Создатель MR является ответственным за этот MR
     *
     * @return true, если автор и ответственный один и тот же человек.
     */
    private boolean isAuthorSameAssignee(Person author, Person assignee) {
        return author.getId().equals(assignee.getId());
    }

    private boolean isBotUserReviewer(MergeRequest savedMergeRequest) {
        final List<Person> reviewers = savedMergeRequest.getReviewers();
        final Long botUserGitlabId = personInformation.getId();

        if (checkNotEmpty(reviewers)) {
            for (Person reviewer : reviewers) {
                if (botUserGitlabId.equals(reviewer.getId())) {
                    return true;
                }
            }
        }
        return false;
    }

    private void sendNotifyNewMrReview(MergeRequest mergeRequest, String projectName) {
        notifyService.send(
                NewMrForReview.builder()
                        .mrId(mergeRequest.getId())
                        .projectName(projectName)
                        .labels(mergeRequest.getLabels())
                        .author(mergeRequest.getAuthor().getName())
                        .description(mergeRequest.getDescription())
                        .title(mergeRequest.getTitle())
                        .url(mergeRequest.getWebUrl())
                        .targetBranch(mergeRequest.getTargetBranch())
                        .sourceBranch(mergeRequest.getSourceBranch())
                        .assignee(mergeRequest.getAssignee().getName())
                        .build()
        );
    }

    private void sendNotifyNewAssignee(MergeRequest mergeRequest, String projectName, String oldAssigneeName) {
        final NewMrForAssignee.NewMrForAssigneeBuilder builder = NewMrForAssignee.builder()
                .mrId(mergeRequest.getId())
                .projectName(projectName)
                .labels(mergeRequest.getLabels())
                .author(mergeRequest.getAuthor().getName())
                .description(mergeRequest.getDescription())
                .title(mergeRequest.getTitle())
                .url(mergeRequest.getWebUrl())
                .targetBranch(mergeRequest.getTargetBranch())
                .sourceBranch(mergeRequest.getSourceBranch())
                .reviewers(mergeRequest.getReviewers().stream().map(Person::getName).toList());

        if (checkNotNull(oldAssigneeName)) {
            builder.oldAssigneeName(oldAssigneeName);

            if (checkNotNull(mergeRequest.getAssignee())) {
                builder.newAssigneeName(mergeRequest.getAssignee().getName());
            }
        }

        notifyService.send(builder.build());
    }

    @Override
    @Transactional
    public MergeRequest update(@NonNull MergeRequest mergeRequest) {
        final MergeRequest oldMergeRequest = repository.findById(mergeRequest.getId())
                .orElseThrow(notFoundException("MergeRequest не найден"));

        mergeRequest.setNotification(oldMergeRequest.isNotification());

        final Long gitlabUserId = personInformation.getId();
        final AssigneeChanged assigneeChanged = AssigneeChanged.valueOf(gitlabUserId, oldMergeRequest.getAssignee(), mergeRequest.getAssignee());
        final ReviewerChanged reviewerChanged = ReviewerChanged.valueOf(gitlabUserId, oldMergeRequest.getReviewers(), mergeRequest.getReviewers());

        mergeRequest.setUserAssignee(assigneeChanged.getNewStatus(oldMergeRequest.isUserAssignee()));
        mergeRequest.setUserReviewer(reviewerChanged.getNewStatus(oldMergeRequest.isUserReviewer()));

        final boolean isChangedMr = !oldMergeRequest.getUpdatedDate().equals(mergeRequest.getUpdatedDate()) || oldMergeRequest.isConflict() != mergeRequest.isConflict();
        final boolean isChangedLinkedEntity = reviewerChanged.isChanged() || assigneeChanged.isChanged();

        if (isChangedMr || isChangedLinkedEntity) {

            if (oldMergeRequest.isNotification()) {
                final Project project = projectService.getByIdOrThrow(mergeRequest.getProjectId());

                if (isChangedMr) {
                    notifyAboutStatus(oldMergeRequest, mergeRequest, project);
                    notifyAboutNewConflict(oldMergeRequest, mergeRequest, project);
                    notifyAboutResolveConflict(oldMergeRequest, mergeRequest, project);
                    notifyAboutUpdate(oldMergeRequest, mergeRequest, project);
                }

                if (isChangedLinkedEntity) {
                    notifyReviewer(reviewerChanged, mergeRequest, project);
                    notifyAssignee(assigneeChanged, oldMergeRequest, mergeRequest, project);
                }
            }
            return repository.save(mergeRequest);
        }

        return oldMergeRequest;
    }

    //TODO [05.12.2022|uPagge]: Добавить уведомление, если происходит удаление

    private void notifyAssignee(AssigneeChanged assigneeChanged, MergeRequest oldMergeRequest, MergeRequest mergeRequest, Project project) {
        switch (assigneeChanged) {
            case BECOME ->
                    sendNotifyNewAssignee(mergeRequest, project.getName(), Optional.ofNullable(oldMergeRequest.getAssignee()).map(Person::getName).orElse(null));
        }
    }
    //TODO [05.12.2022|uPagge]: Добавить уведомление, если происходит удаление ревьювера

    private void notifyReviewer(ReviewerChanged reviewerChanged, MergeRequest mergeRequest, Project project) {
        switch (reviewerChanged) {
            case BECOME -> sendNotifyNewMrReview(mergeRequest, project.getName());
        }
    }

    @Override
    @Transactional
    public List<MergeRequest> updateAll(@NonNull List<MergeRequest> mergeRequests) {
        return mergeRequests.stream()
                .map(this::update)
                .collect(Collectors.toList());
    }

    @Override
    public Set<IdAndStatusPr> getAllId(Set<MergeRequestState> statuses) {
        return repository.findAllIdByStateIn(statuses);
    }

    @Override
    public List<MergeRequestForDiscussion> getAllForDiscussion() {
        return repository.findAllForDiscussion();
    }

    @Override
    public ExistContainer<MergeRequest, Long> existsById(@NonNull Set<Long> mergeRequestIds) {
        final List<MergeRequest> existsEntity = repository.findAllById(mergeRequestIds);
        final Set<Long> existsIds = existsEntity.stream().map(MergeRequest::getId).collect(Collectors.toSet());
        if (existsIds.containsAll(mergeRequestIds)) {
            return ExistContainer.allFind(existsEntity);
        } else {
            final Set<Long> noExistsId = mergeRequestIds.stream()
                    .filter(id -> !existsIds.contains(id))
                    .collect(Collectors.toSet());
            return ExistContainer.notAllFind(existsEntity, noExistsId);
        }
    }

    @Override
    public List<MergeRequest> createAll(List<MergeRequest> newMergeRequests) {
        return newMergeRequests.stream()
                .map(this::create)
                .toList();
    }

    @Override
    public List<MergeRequest> getAllByReviewerId(@NonNull Long personId) {
        return repository.findAllByReviewerId(personId);
    }

    @Override
    public void cleanOld() {
        log.debug("Старт очистки старых MR");
        repository.deleteByStates(DELETE_STATES);
        log.debug("Конец очистки старых MR");
    }

    @Override
    @Transactional(readOnly = true)
    public Set<Long> getAllIds() {
        return repository.findAllIds();
    }

    @Override
    @Transactional
    public void notification(boolean enable, @NonNull Long mrId) {
        repository.notification(enable, mrId);
    }

    @Override
    @Transactional
    public void notificationByProjectId(boolean enable, @NonNull Set<Long> projectIds) {
        repository.notificationByProjectId(enable, projectIds);
    }

    private void notifyAboutUpdate(MergeRequest oldMergeRequest, MergeRequest mergeRequest, Project project) {
        final Long botUserGitlabId = personInformation.getId();
        if (
                !botUserGitlabId.equals(mergeRequest.getAuthor().getId()) // Автор MR не пользователь приложения
                && !oldMergeRequest.getDateLastCommit().equals(mergeRequest.getDateLastCommit()) // Изменилась дата последнего коммита
                && !mergeRequest.isConflict() // MR не находится в состоянии конфликта
                && !botUserGitlabId.equals(oldMergeRequest.getAuthor().getId()) // и MR создан НЕ пользователем бота
        ) {
            long allTask = 0;
            long resolvedTask = 0;
            long allYouTasks = 0;
            long resolvedYouTask = 0;

            final List<Discussion> discussions = discussionService.getAllByMergeRequestId(oldMergeRequest.getId());
            for (Discussion discussion : discussions) {
                if (checkNotNull(discussion.getResponsible())) {
                    final boolean isBotUserAuthorDiscussion = botUserGitlabId.equals(discussion.getFirstNote().getAuthor().getId());
                    allTask += 1;
                    if (isBotUserAuthorDiscussion) {
                        allYouTasks += 1;
                    }
                    if (TRUE.equals(discussion.getResolved())) {
                        resolvedTask += 1;
                        if (isBotUserAuthorDiscussion) {
                            resolvedYouTask += 1;
                        }
                    }
                }
            }

            final UpdateMrNotify.UpdateMrNotifyBuilder notifyBuilder = UpdateMrNotify.builder()
                    .mrId(oldMergeRequest.getId())
                    .author(oldMergeRequest.getAuthor().getName())
                    .name(oldMergeRequest.getTitle())
                    .projectName(project.getName())
                    .url(oldMergeRequest.getWebUrl())
                    .allTasks(allTask)
                    .allResolvedTasks(resolvedTask)
                    .personTasks(allYouTasks)
                    .personResolvedTasks(resolvedYouTask);

            if (oldMergeRequest.isConflict() && !mergeRequest.isConflict()) {
                notifyBuilder.comment("The conflict has been resolved");
            }

            notifyService.send(notifyBuilder.build());
        }
    }

    protected void notifyAboutNewConflict(MergeRequest oldMergeRequest, MergeRequest mergeRequest, Project project) {
        final Long gitlabUserId = personInformation.getId();
        if (
                !oldMergeRequest.isConflict() // У старого MR не было конфликта
                && mergeRequest.isConflict() // А у нового есть
                && gitlabUserId.equals(oldMergeRequest.getAuthor().getId()) // и MR создан пользователем бота
        ) {
            notifyService.send(
                    ConflictMrNotify.builder()
                            .mrId(oldMergeRequest.getId())
                            .sourceBranch(oldMergeRequest.getSourceBranch())
                            .name(mergeRequest.getTitle())
                            .url(mergeRequest.getWebUrl())
                            .projectKey(project.getName())
                            .build()
            );
        }
    }

    private void notifyAboutResolveConflict(MergeRequest oldMergeRequest, MergeRequest mergeRequest, Project project) {
        final Long gitlabUserId = personInformation.getId();
        if (
                oldMergeRequest.isConflict() // У старого MR был конфликт
                && !mergeRequest.isConflict() // А у нового нет
                && gitlabUserId.equals(oldMergeRequest.getAuthor().getId()) // и MR создан пользователем бота
        ) {
            notifyService.send(
                    ConflictResolveMrNotify.builder()
                            .mrId(oldMergeRequest.getId())
                            .sourceBranch(oldMergeRequest.getSourceBranch())
                            .name(mergeRequest.getTitle())
                            .url(mergeRequest.getWebUrl())
                            .projectKey(project.getName())
                            .build()
            );
        }
    }

    protected void notifyAboutStatus(MergeRequest oldMergeRequest, MergeRequest newMergeRequest, Project project) {
        final MergeRequestState oldStatus = oldMergeRequest.getState();
        final MergeRequestState newStatus = newMergeRequest.getState();
        final Long gitlabUserId = personInformation.getId();
        if (
                !oldStatus.equals(newStatus) // статус изменился
                && gitlabUserId.equals(oldMergeRequest.getAuthor().getId()) // создатель MR является пользователем бота
        ) {
            notifyService.send(
                    StatusMrNotify.builder()
                            .mrId(oldMergeRequest.getId())
                            .name(newMergeRequest.getTitle())
                            .url(oldMergeRequest.getWebUrl())
                            .projectName(project.getName())
                            .newStatus(newStatus)
                            .oldStatus(oldStatus)
                            .build()
            );
        }
    }

}
