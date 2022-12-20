package dev.struchkov.bot.gitlab.core.service.impl;

import dev.struchkov.bot.gitlab.context.domain.AssigneeChanged;
import dev.struchkov.bot.gitlab.context.domain.ExistContainer;
import dev.struchkov.bot.gitlab.context.domain.IdAndStatusPr;
import dev.struchkov.bot.gitlab.context.domain.MergeRequestState;
import dev.struchkov.bot.gitlab.context.domain.PersonInformation;
import dev.struchkov.bot.gitlab.context.domain.ReviewerChanged;
import dev.struchkov.bot.gitlab.context.domain.entity.Discussion;
import dev.struchkov.bot.gitlab.context.domain.entity.MergeRequest;
import dev.struchkov.bot.gitlab.context.domain.entity.Person;
import dev.struchkov.bot.gitlab.context.domain.entity.Project;
import dev.struchkov.bot.gitlab.context.domain.filter.MergeRequestFilter;
import dev.struchkov.bot.gitlab.context.domain.notify.mergerequest.ConflictPrNotify;
import dev.struchkov.bot.gitlab.context.domain.notify.mergerequest.NewPrNotify;
import dev.struchkov.bot.gitlab.context.domain.notify.mergerequest.StatusPrNotify;
import dev.struchkov.bot.gitlab.context.domain.notify.mergerequest.UpdatePrNotify;
import dev.struchkov.bot.gitlab.context.repository.MergeRequestRepository;
import dev.struchkov.bot.gitlab.context.service.DiscussionService;
import dev.struchkov.bot.gitlab.context.service.MergeRequestsService;
import dev.struchkov.bot.gitlab.context.service.NotifyService;
import dev.struchkov.bot.gitlab.context.service.ProjectService;
import dev.struchkov.bot.gitlab.core.service.impl.filter.MergeRequestFilterService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static dev.struchkov.haiti.context.exception.NotFoundException.notFoundException;
import static dev.struchkov.haiti.utils.Checker.checkNotEmpty;
import static dev.struchkov.haiti.utils.Checker.checkNotNull;
import static java.lang.Boolean.TRUE;

@Service
@RequiredArgsConstructor
public class MergeRequestsServiceImpl implements MergeRequestsService {

    private final NotifyService notifyService;
    private final MergeRequestRepository repository;
    private final MergeRequestFilterService filterService;
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
                if (botUserReviewer) sendNotifyAboutNewMr(savedMergeRequest, projectName);
                if (botUserAssignee) sendNotifyAboutAssignee(mergeRequest, projectName);
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

    private void sendNotifyAboutNewMr(MergeRequest mergeRequest, String projectName) {
        notifyService.send(
                NewPrNotify.builder()
                        .projectName(projectName)
                        .labels(mergeRequest.getLabels())
                        .author(mergeRequest.getAuthor().getName())
                        .description(mergeRequest.getDescription())
                        .title(mergeRequest.getTitle())
                        .url(mergeRequest.getWebUrl())
                        .targetBranch(mergeRequest.getTargetBranch())
                        .sourceBranch(mergeRequest.getSourceBranch())
                        .build()
        );
    }

    private void sendNotifyAboutAssignee(MergeRequest mergeRequest, String projectName) {
        notifyService.send(
                NewPrNotify.builder()
                        .projectName(projectName)
                        .labels(mergeRequest.getLabels())
                        .author(mergeRequest.getAuthor().getName())
                        .description(mergeRequest.getDescription())
                        .title(mergeRequest.getTitle())
                        .url(mergeRequest.getWebUrl())
                        .targetBranch(mergeRequest.getTargetBranch())
                        .sourceBranch(mergeRequest.getSourceBranch())
                        .build()
        );
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

        final boolean isChangedMr = !oldMergeRequest.getUpdatedDate().equals(mergeRequest.getUpdatedDate())
                || oldMergeRequest.isConflict() != mergeRequest.isConflict();
        final boolean isChangedLinkedEntity = reviewerChanged.isChanged() || assigneeChanged.isChanged();

        if (isChangedMr || isChangedLinkedEntity) {
            final MergeRequest savedMergeRequest = repository.save(mergeRequest);

            if (oldMergeRequest.isNotification()) {
                final Project project = projectService.getByIdOrThrow(mergeRequest.getProjectId());

                if (isChangedMr) {
                    notifyAboutStatus(oldMergeRequest, savedMergeRequest, project);
                    notifyAboutConflict(oldMergeRequest, savedMergeRequest, project);
                    notifyAboutUpdate(oldMergeRequest, savedMergeRequest, project);
                }

                if (isChangedLinkedEntity) {
                    notifyReviewer(reviewerChanged, savedMergeRequest, project);
                    notifyAssignee(assigneeChanged, savedMergeRequest, project);
                }
            }
            return savedMergeRequest;
        }

        return oldMergeRequest;
    }


    //TODO [05.12.2022|uPagge]: Добавить уведомление, если происходит удаление
    private void notifyAssignee(AssigneeChanged assigneeChanged, MergeRequest mergeRequest, Project project) {
        switch (assigneeChanged) {
            case BECOME -> sendNotifyAboutNewMr(mergeRequest, project.getName());
        }
    }

    //TODO [05.12.2022|uPagge]: Добавить уведомление, если происходит удаление ревьювера
    //TODO [05.12.2022|uPagge]: Заменить тип уведомления на самостоятельный
    private void notifyReviewer(ReviewerChanged reviewerChanged, MergeRequest mergeRequest, Project project) {
        switch (reviewerChanged) {
            case BECOME -> sendNotifyAboutNewMr(mergeRequest, project.getName());
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
    public Page<MergeRequest> getAll(Pageable pagination) {
        return repository.findAll(pagination);
    }

    @Override
    public Page<MergeRequest> getAll(@NonNull MergeRequestFilter filter, Pageable pagination) {
        return filterService.getAll(filter, pagination);
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
    @Transactional
    public void deleteAllById(@NonNull Set<Long> mergeRequestIds) {
        repository.deleteByIds(mergeRequestIds);
    }

    @Override
    public List<MergeRequest> getAllByReviewerId(@NonNull Long personId) {
        return repository.findAllByReviewerId(personId);
    }

    private void notifyAboutUpdate(MergeRequest oldMergeRequest, MergeRequest mergeRequest, Project project) {
        final Long botUserGitlabId = personInformation.getId();

        if (
                !botUserGitlabId.equals(mergeRequest.getAuthor().getId()) // Автор MR не пользователь приложения
                        && !oldMergeRequest.getDateLastCommit().equals(mergeRequest.getDateLastCommit()) // Изменилась дата последнего коммита
                        && !mergeRequest.isConflict() // MR не находится в состоянии конфликта
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
            notifyService.send(
                    UpdatePrNotify.builder()
                            .author(oldMergeRequest.getAuthor().getName())
                            .name(oldMergeRequest.getTitle())
                            .projectKey(project.getName())
                            .url(oldMergeRequest.getWebUrl())
                            .allTasks(allTask)
                            .allResolvedTasks(resolvedTask)
                            .personTasks(allYouTasks)
                            .personResolvedTasks(resolvedYouTask)
                            .build()
            );
        }
    }

    protected void notifyAboutConflict(MergeRequest oldMergeRequest, MergeRequest mergeRequest, Project project) {
        final Long gitlabUserId = personInformation.getId();
        if (
                !oldMergeRequest.isConflict() // У старого MR не было конфликта
                        && mergeRequest.isConflict() // А у нового есть
                        && gitlabUserId.equals(oldMergeRequest.getAuthor().getId()) // и MR создан пользователем бота
        ) {
            notifyService.send(
                    ConflictPrNotify.builder()
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
                    StatusPrNotify.builder()
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
