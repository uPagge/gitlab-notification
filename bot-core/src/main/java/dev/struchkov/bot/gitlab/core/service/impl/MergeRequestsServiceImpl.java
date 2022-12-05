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
import dev.struchkov.bot.gitlab.context.domain.notify.pullrequest.ConflictPrNotify;
import dev.struchkov.bot.gitlab.context.domain.notify.pullrequest.NewPrNotify;
import dev.struchkov.bot.gitlab.context.domain.notify.pullrequest.StatusPrNotify;
import dev.struchkov.bot.gitlab.context.domain.notify.pullrequest.UpdatePrNotify;
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
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static dev.struchkov.haiti.context.exception.NotFoundException.notFoundException;
import static dev.struchkov.haiti.utils.Checker.checkNotEmpty;
import static dev.struchkov.haiti.utils.Checker.checkNotNull;
import static dev.struchkov.haiti.utils.Checker.checkNull;
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
        mergeRequest.setNotification(true);

        final MergeRequest newMergeRequest = repository.save(mergeRequest);

        notifyNewMergeRequest(newMergeRequest);

        return newMergeRequest;
    }

    /**
     * Уведомление о новом MergeRequest.
     *
     * @param savedMergeRequest сохраненный в базу новый MergeRequest.
     */
    private void notifyNewMergeRequest(MergeRequest savedMergeRequest) {
        notifyUserAboutNewPullRequestIfHeIsReviewer(savedMergeRequest);
        notifyUserAboutNewPullRequestIfHeIsAssignee(savedMergeRequest);
    }

    private void notifyUserAboutNewPullRequestIfHeIsAssignee(MergeRequest savedMergeRequest) {
        final Long gitlabUserId = personInformation.getId();
        final Person assignee = savedMergeRequest.getAssignee();
        final Person author = savedMergeRequest.getAuthor();

        if (checkNotNull(assignee)) {
            if (gitlabUserId.equals(assignee.getId()) && !isAuthorSameAssignee(author, assignee)) {
                final String projectName = projectService.getByIdOrThrow(savedMergeRequest.getProjectId()).getName();
                if (!savedMergeRequest.isConflict()) {
                    //TODO [05.12.2022|uPagge]: Заменить уведомление. Нужно создать новое уведомление, если пользователя назначали ответственным
                    notifyService.send(
                            NewPrNotify.builder()
                                    .projectName(projectName)
                                    .labels(savedMergeRequest.getLabels())
                                    .author(author.getName())
                                    .description(savedMergeRequest.getDescription())
                                    .title(savedMergeRequest.getTitle())
                                    .url(savedMergeRequest.getWebUrl())
                                    .targetBranch(savedMergeRequest.getTargetBranch())
                                    .sourceBranch(savedMergeRequest.getSourceBranch())
                                    .build()
                    );
                }
            }
        }
    }

    /**
     * Создатель MR является ответственным за этот MR
     *
     * @return true, если автор и ответственный один и тот же человек.
     */
    private boolean isAuthorSameAssignee(Person author, Person assignee) {
        return author.getId().equals(assignee.getId());
    }

    private void notifyUserAboutNewPullRequestIfHeIsReviewer(MergeRequest savedMergeRequest) {
        final List<Person> reviewers = savedMergeRequest.getReviewers();
        final Long gitlabUserId = personInformation.getId();

        if (checkNotEmpty(reviewers)) {
            final boolean isUserInReviewers = reviewers.stream()
                    .anyMatch(reviewer -> gitlabUserId.equals(reviewer.getId()));
            if (isUserInReviewers) {
                final String projectName = projectService.getByIdOrThrow(savedMergeRequest.getProjectId()).getName();
                if (!savedMergeRequest.isConflict()) {
                    sendNotifyAboutNewMr(savedMergeRequest, projectName);
                }
            }
        }
    }

    private void sendNotifyAboutNewMr(MergeRequest savedMergeRequest, String projectName) {
        notifyService.send(
                NewPrNotify.builder()
                        .projectName(projectName)
                        .labels(savedMergeRequest.getLabels())
                        .author(savedMergeRequest.getAuthor().getName())
                        .description(savedMergeRequest.getDescription())
                        .title(savedMergeRequest.getTitle())
                        .url(savedMergeRequest.getWebUrl())
                        .targetBranch(savedMergeRequest.getTargetBranch())
                        .sourceBranch(savedMergeRequest.getSourceBranch())
                        .build()
        );
    }

    @Override
    @Transactional
    public MergeRequest update(@NonNull MergeRequest mergeRequest) {
        final MergeRequest oldMergeRequest = repository.findById(mergeRequest.getId())
                .orElseThrow(notFoundException("MergeRequest не найден"));

        final Boolean notification = mergeRequest.getNotification();
        if (checkNull(notification)) {
            mergeRequest.setNotification(oldMergeRequest.getNotification());
        }

        final Long gitlabUserId = personInformation.getId();
        final AssigneeChanged assigneeChanged = AssigneeChanged.valueOf(gitlabUserId, oldMergeRequest.getAssignee(), mergeRequest.getAssignee());
        final ReviewerChanged reviewerChanged = ReviewerChanged.valueOf(gitlabUserId, oldMergeRequest.getReviewers(), mergeRequest.getReviewers());

        final boolean isChangedMr =
                !oldMergeRequest.getUpdatedDate().equals(mergeRequest.getUpdatedDate())
                        || oldMergeRequest.isConflict() != mergeRequest.isConflict();
        final boolean isChangedLinkedEntity = reviewerChanged.isChanged() || assigneeChanged.isChanged();

        if (isChangedMr || isChangedLinkedEntity) {
            final Project project = projectService.getByIdOrThrow(mergeRequest.getProjectId());

            if (TRUE.equals(notification) && isChangedMr) {
                notifyAboutStatus(oldMergeRequest, mergeRequest, project);
                notifyAboutConflict(oldMergeRequest, mergeRequest, project);
                notifyAboutUpdate(oldMergeRequest, mergeRequest, project);
            }

            if (TRUE.equals(notification) && isChangedLinkedEntity) {
                notifyReviewer(reviewerChanged, mergeRequest, project);
                notifyAssignee(assigneeChanged, mergeRequest, project);
            }

            return repository.save(mergeRequest);
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
    public void deleteAllById(@NonNull Set<Long> mergeRequestIds) {
        repository.deleteByIds(mergeRequestIds);
    }

    @Override
    public List<MergeRequest> getAllByReviewerId(@NonNull Long personId) {
        return repository.findAllByReviewerId(personId);
    }

    private void notifyAboutUpdate(MergeRequest oldMergeRequest, MergeRequest mergeRequest, Project project) {
        final Long gitlabUserId = personInformation.getId();

        if (
                !gitlabUserId.equals(mergeRequest.getAuthor().getId()) // Автор MR не пользователь приложения
                        && !oldMergeRequest.getDateLastCommit().equals(mergeRequest.getDateLastCommit()) // Изменилась дата последнего коммита
                        && !mergeRequest.isConflict() // MR не находится в состоянии конфликта
        ) {
            final List<Discussion> discussions = discussionService.getAllByMergeRequestId(oldMergeRequest.getId())
                    .stream()
                    .filter(discussion -> Objects.nonNull(discussion.getResponsible()))
                    .toList();
            final long allTask = discussions.size();
            final long resolvedTask = discussions.stream()
                    .filter(Discussion::getResolved)
                    .count();
            final long allYouTasks = discussions.stream()
                    .filter(discussion -> gitlabUserId.equals(discussion.getFirstNote().getAuthor().getId()))
                    .count();
            final long resolvedYouTask = discussions.stream()
                    .filter(discussion -> gitlabUserId.equals(discussion.getFirstNote().getAuthor().getId()) && discussion.getResolved())
                    .count();
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
