package org.sadtech.bot.gitlab.core.service.impl;

import lombok.NonNull;
import org.sadtech.bot.gitlab.context.domain.IdAndStatusPr;
import org.sadtech.bot.gitlab.context.domain.MergeRequestState;
import org.sadtech.bot.gitlab.context.domain.PersonInformation;
import org.sadtech.bot.gitlab.context.domain.entity.Discussion;
import org.sadtech.bot.gitlab.context.domain.entity.MergeRequest;
import org.sadtech.bot.gitlab.context.domain.entity.Project;
import org.sadtech.bot.gitlab.context.domain.filter.MergeRequestFilter;
import org.sadtech.bot.gitlab.context.domain.notify.pullrequest.ConflictPrNotify;
import org.sadtech.bot.gitlab.context.domain.notify.pullrequest.NewPrNotify;
import org.sadtech.bot.gitlab.context.domain.notify.pullrequest.StatusPrNotify;
import org.sadtech.bot.gitlab.context.domain.notify.pullrequest.UpdatePrNotify;
import org.sadtech.bot.gitlab.context.repository.MergeRequestRepository;
import org.sadtech.bot.gitlab.context.service.DiscussionService;
import org.sadtech.bot.gitlab.context.service.MergeRequestsService;
import org.sadtech.bot.gitlab.context.service.NotifyService;
import org.sadtech.bot.gitlab.context.service.PersonService;
import org.sadtech.bot.gitlab.context.service.ProjectService;
import org.sadtech.haiti.context.exception.NotFoundException;
import org.sadtech.haiti.context.page.Pagination;
import org.sadtech.haiti.context.page.Sheet;
import org.sadtech.haiti.context.service.simple.FilterService;
import org.sadtech.haiti.core.service.AbstractSimpleManagerService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class MergeRequestsServiceImpl extends AbstractSimpleManagerService<MergeRequest, Long> implements MergeRequestsService {

    private final NotifyService notifyService;
    private final MergeRequestRepository mergeRequestRepository;
    private final PersonService personService;
    private final FilterService<MergeRequest, MergeRequestFilter> filterService;
    private final ProjectService projectService;
    private final DiscussionService discussionService;

    private final PersonInformation personInformation;

    protected MergeRequestsServiceImpl(
            MergeRequestRepository mergeRequestRepository,
            NotifyService notifyService,
            PersonService personService,
            @Qualifier("mergeRequestFilterService") FilterService<MergeRequest, MergeRequestFilter> filterService,
            ProjectService projectService,
            DiscussionService discussionService, PersonInformation personInformation
    ) {
        super(mergeRequestRepository);
        this.notifyService = notifyService;
        this.mergeRequestRepository = mergeRequestRepository;
        this.personService = personService;
        this.filterService = filterService;
        this.projectService = projectService;
        this.discussionService = discussionService;
        this.personInformation = personInformation;
    }

    @Override
    public MergeRequest create(@NonNull MergeRequest mergeRequest) {
        if (mergeRequest.getAssignee() != null) {
            personService.create(mergeRequest.getAssignee());
        }
        personService.create(mergeRequest.getAuthor());

        mergeRequest.setNotification(true);

        final MergeRequest newMergeRequest = mergeRequestRepository.save(mergeRequest);

        notifyNewPr(newMergeRequest);

        return newMergeRequest;
    }

    private void notifyNewPr(MergeRequest newMergeRequest) {
        if (!personInformation.getId().equals(newMergeRequest.getAuthor().getId())) {

            final String projectName = projectService.getById(newMergeRequest.getProjectId())
                    .orElseThrow(() -> new NotFoundException("Проект не найден"))
                    .getName();
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

        final MergeRequest oldMergeRequest = mergeRequestRepository.findById(mergeRequest.getId())
                .orElseThrow(() -> new NotFoundException("МержРеквест не найден"));

        if (mergeRequest.getNotification() == null) {
            mergeRequest.setNotification(oldMergeRequest.getNotification());
        }

        if (!oldMergeRequest.getUpdatedDate().equals(mergeRequest.getUpdatedDate()) || oldMergeRequest.isConflict() != mergeRequest.isConflict()) {
            final Project project = projectService.getById(mergeRequest.getProjectId())
                    .orElseThrow(() -> new NotFoundException("Проект не найден"));

            if (Boolean.TRUE.equals(oldMergeRequest.getNotification())) {
                notifyStatus(oldMergeRequest, mergeRequest, project);
                notifyConflict(oldMergeRequest, mergeRequest, project);
                notifyUpdate(oldMergeRequest, mergeRequest, project);
            }

            return mergeRequestRepository.save(mergeRequest);
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
                    .collect(Collectors.toList());
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
        return mergeRequestRepository.findAllIdByStateIn(statuses);
    }

    @Override
    public Sheet<MergeRequest> getAll(@NonNull MergeRequestFilter filter, Pagination pagination) {
        return filterService.getAll(filter, pagination);
    }

    @Override
    public Optional<MergeRequest> getFirst(@NonNull MergeRequestFilter mergeRequestFilter) {
        return filterService.getFirst(mergeRequestFilter);
    }

    @Override
    public boolean exists(@NonNull MergeRequestFilter filter) {
        return filterService.exists(filter);
    }

    @Override
    public long count(@NonNull MergeRequestFilter mergeRequestFilter) {
        return filterService.count(mergeRequestFilter);
    }

}
