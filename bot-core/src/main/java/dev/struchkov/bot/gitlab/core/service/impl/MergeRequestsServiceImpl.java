package dev.struchkov.bot.gitlab.core.service.impl;

import dev.struchkov.bot.gitlab.context.domain.ExistsContainer;
import dev.struchkov.bot.gitlab.context.domain.IdAndStatusPr;
import dev.struchkov.bot.gitlab.context.domain.MergeRequestState;
import dev.struchkov.bot.gitlab.context.domain.PersonInformation;
import dev.struchkov.bot.gitlab.context.domain.entity.Discussion;
import dev.struchkov.bot.gitlab.context.domain.entity.MergeRequest;
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
import dev.struchkov.bot.gitlab.context.service.PersonService;
import dev.struchkov.bot.gitlab.context.service.ProjectService;
import dev.struchkov.bot.gitlab.core.service.impl.filter.MergeRequestFilterService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static dev.struchkov.haiti.context.exception.NotFoundException.notFoundException;
import static java.lang.Boolean.TRUE;

@Service
@RequiredArgsConstructor
public class MergeRequestsServiceImpl implements MergeRequestsService {

    private final NotifyService notifyService;
    private final MergeRequestRepository repository;
    private final PersonService personService;
    private final MergeRequestFilterService filterService;
    private final ProjectService projectService;
    private final DiscussionService discussionService;

    private final PersonInformation personInformation;

    @Override
    public MergeRequest create(@NonNull MergeRequest mergeRequest) {
        if (mergeRequest.getAssignee() != null) {
            personService.create(mergeRequest.getAssignee());
        }
        personService.create(mergeRequest.getAuthor());

        mergeRequest.setNotification(true);

        final MergeRequest newMergeRequest = repository.save(mergeRequest);

        notifyNewPr(newMergeRequest);

        return newMergeRequest;
    }

    private void notifyNewPr(MergeRequest newMergeRequest) {
        if (!personInformation.getId().equals(newMergeRequest.getAuthor().getId())) {

            final String projectName = projectService.getByIdOrThrow(newMergeRequest.getProjectId()).getName();
            if (!newMergeRequest.isConflict()) {
                notifyService.send(
                        NewPrNotify.builder()
                                .projectName(projectName)
                                .labels(newMergeRequest.getLabels())
                                .author(newMergeRequest.getAuthor().getName())
                                .description(newMergeRequest.getDescription())
                                .title(newMergeRequest.getTitle())
                                .url(newMergeRequest.getWebUrl())
                                .targetBranch(newMergeRequest.getTargetBranch())
                                .sourceBranch(newMergeRequest.getSourceBranch())
                                .build()
                );
            }

        }
    }

    @Override
    public MergeRequest update(@NonNull MergeRequest mergeRequest) {
        if (mergeRequest.getAssignee() != null) {
            personService.create(mergeRequest.getAssignee());
        }
        personService.create(mergeRequest.getAuthor());

        final MergeRequest oldMergeRequest = repository.findById(mergeRequest.getId())
                .orElseThrow(notFoundException("МержРеквест не найден"));

        if (mergeRequest.getNotification() == null) {
            mergeRequest.setNotification(oldMergeRequest.getNotification());
        }

        if (!oldMergeRequest.getUpdatedDate().equals(mergeRequest.getUpdatedDate()) || oldMergeRequest.isConflict() != mergeRequest.isConflict()) {
            final Project project = projectService.getByIdOrThrow(mergeRequest.getProjectId());

            if (TRUE.equals(oldMergeRequest.getNotification())) {
                notifyStatus(oldMergeRequest, mergeRequest, project);
                notifyConflict(oldMergeRequest, mergeRequest, project);
                notifyUpdate(oldMergeRequest, mergeRequest, project);
            }

            return repository.save(mergeRequest);
        }
        return oldMergeRequest;
    }

    private void notifyUpdate(MergeRequest oldMergeRequest, MergeRequest mergeRequest, Project project) {
        if (
                !personInformation.getId().equals(mergeRequest.getAuthor().getId())
                        && !oldMergeRequest.getDateLastCommit().equals(mergeRequest.getDateLastCommit())
                        && !mergeRequest.isConflict()
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
                    .filter(discussion -> personInformation.getId().equals(discussion.getFirstNote().getAuthor().getId()))
                    .count();
            final long resolvedYouTask = discussions.stream()
                    .filter(discussion -> personInformation.getId().equals(discussion.getFirstNote().getAuthor().getId()) && discussion.getResolved())
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

    protected void notifyConflict(MergeRequest oldMergeRequest, MergeRequest mergeRequest, Project project) {
        if (
                !oldMergeRequest.isConflict()
                        && mergeRequest.isConflict()
                        && personInformation.getId().equals(oldMergeRequest.getAuthor().getId())
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

    protected void notifyStatus(MergeRequest oldMergeRequest, MergeRequest newMergeRequest, Project project) {
        final MergeRequestState oldStatus = oldMergeRequest.getState();
        final MergeRequestState newStatus = newMergeRequest.getState();
        if (
                !oldStatus.equals(newStatus)
                        && oldMergeRequest.getAuthor().getId().equals(personInformation.getId())
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
    public ExistsContainer<MergeRequest, Long> existsById(@NonNull Set<Long> mergeRequestIds) {
        final List<MergeRequest> existsEntity = repository.findAllById(mergeRequestIds);
        final Set<Long> existsIds = existsEntity.stream().map(MergeRequest::getId).collect(Collectors.toSet());
        if (existsIds.containsAll(mergeRequestIds)) {
            return ExistsContainer.allFind(existsEntity);
        } else {
            final Set<Long> noExistsId = mergeRequestIds.stream()
                    .filter(id -> !existsIds.contains(id))
                    .collect(Collectors.toSet());
            return ExistsContainer.notAllFind(existsEntity, noExistsId);
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

}
