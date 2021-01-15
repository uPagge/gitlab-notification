package org.sadtech.bot.gitlab.core.service.impl;

import lombok.NonNull;
import org.sadtech.bot.gitlab.context.domain.IdAndStatusPr;
import org.sadtech.bot.gitlab.context.domain.MergeRequestState;
import org.sadtech.bot.gitlab.context.domain.PersonInformation;
import org.sadtech.bot.gitlab.context.domain.entity.MergeRequest;
import org.sadtech.bot.gitlab.context.domain.entity.MergeRequestMini;
import org.sadtech.bot.gitlab.context.domain.entity.Project;
import org.sadtech.bot.gitlab.context.domain.filter.PullRequestFilter;
import org.sadtech.bot.gitlab.context.domain.notify.pullrequest.ConflictPrNotify;
import org.sadtech.bot.gitlab.context.domain.notify.pullrequest.NewPrNotify;
import org.sadtech.bot.gitlab.context.domain.notify.pullrequest.StatusPrNotify;
import org.sadtech.bot.gitlab.context.repository.MergeRequestRepository;
import org.sadtech.bot.gitlab.context.service.MergeRequestsService;
import org.sadtech.bot.gitlab.context.service.NotifyService;
import org.sadtech.bot.gitlab.context.service.PersonService;
import org.sadtech.bot.gitlab.context.service.ProjectService;
import org.sadtech.haiti.context.exception.NotFoundException;
import org.sadtech.haiti.context.page.Pagination;
import org.sadtech.haiti.context.page.Sheet;
import org.sadtech.haiti.core.service.AbstractSimpleManagerService;
import org.sadtech.haiti.filter.FilterService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

@Service
public class MergeRequestsServiceImpl extends AbstractSimpleManagerService<MergeRequest, Long> implements MergeRequestsService {

    private final NotifyService notifyService;
    private final MergeRequestRepository mergeRequestRepository;
    private final PersonService personService;
    private final FilterService<MergeRequest, PullRequestFilter> filterService;
    private final ProjectService projectService;

    private final PersonInformation personInformation;

    protected MergeRequestsServiceImpl(
            MergeRequestRepository mergeRequestRepository,
            NotifyService notifyService,
            PersonService personService,
            @Qualifier("mergeRequestFilterService") FilterService<MergeRequest, PullRequestFilter> filterService,
            ProjectService projectService,
            PersonInformation personInformation
    ) {
        super(mergeRequestRepository);
        this.notifyService = notifyService;
        this.mergeRequestRepository = mergeRequestRepository;
        this.personService = personService;
        this.filterService = filterService;
        this.projectService = projectService;
        this.personInformation = personInformation;
    }

    @Override
    public MergeRequest create(@NonNull MergeRequest mergeRequest) {
        personService.create(mergeRequest.getAuthor());
        personService.create(mergeRequest.getAssignee());

        final MergeRequest newMergeRequest = mergeRequestRepository.save(mergeRequest);

        if (!personInformation.getId().equals(newMergeRequest.getAuthor().getId())) {
            final String projectName = projectService.getById(newMergeRequest.getProjectId())
                    .orElseThrow(() -> new NotFoundException("Проект не найден"))
                    .getName();
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
        return newMergeRequest;
    }

    @Override
    public MergeRequest update(@NonNull MergeRequest mergeRequest) {
        final MergeRequest oldMergeRequest = mergeRequestRepository.findById(mergeRequest.getId())
                .orElseThrow(() -> new NotFoundException("МержРеквест не найден"));

//        forgottenNotification(oldMergeRequest);

        final Project project = projectService.getById(mergeRequest.getProjectId())
                .orElseThrow(() -> new NotFoundException("Проект не найден"));
        notifyStatus(oldMergeRequest, mergeRequest, project);
        notifyConflict(oldMergeRequest, mergeRequest, project);

        return mergeRequestRepository.save(mergeRequest);
    }

    protected void forgottenNotification(MergeRequest mergeRequest) {
//        if (LocalDateTime.now().isAfter(mergeRequest.getUpdateDate().plusHours(2L))) {
//            final Set<String> smartReviewers = mergeRequest.getReviewers().stream()
//                    .filter(
//                            reviewer -> ReviewerStatus.NEEDS_WORK.equals(reviewer.getStatus())
//                                    && LocalDateTime.now().isAfter(reviewer.getDateChange().plusHours(2L))
//                                    && reviewer.getDateSmartNotify() == null
//                    )
//                    .peek(reviewer -> reviewer.setDateSmartNotify(LocalDateTime.now()))
//                    .map(Reviewer::getPersonLogin)
//                    .collect(Collectors.toSet());
//            if (!smartReviewers.isEmpty()) {
//                notifyService.send(
//                        ForgottenSmartPrNotify.builder()
//                                .projectKey(mergeRequest.getProjectKey())
//                                .repositorySlug(mergeRequest.getRepositorySlug())
//                                .recipients(smartReviewers)
//                                .title(mergeRequest.getTitle())
//                                .url(mergeRequest.getUrl())
//                                .build()
//                );
//            }
//        }
    }

    protected void notifyConflict(MergeRequest oldMergeRequest, MergeRequest mergeRequest, Project project) {
        if (
                !oldMergeRequest.isConflict()
                        && mergeRequest.isConflict()
                        && oldMergeRequest.getAuthor().getId().equals(personInformation.getId())
        ) {
            notifyService.send(
                    ConflictPrNotify.builder()
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

    protected boolean enoughTimHasPassedSinceUpdatePr(LocalDateTime updateDate) {
        return LocalDateTime.now().isAfter(updateDate.plusHours(4L));
    }

    @Override
    public Set<IdAndStatusPr> getAllId(Set<MergeRequestState> statuses) {
        return mergeRequestRepository.findAllIdByStateIn(statuses);
    }

    @Override
    public Optional<MergeRequestMini> getMiniInfo(@NonNull Long pullRequestId) {
        return mergeRequestRepository.findMiniInfoById(pullRequestId);
    }

    @Override
    public Sheet<MergeRequest> getAll(@NonNull PullRequestFilter filter, Pagination pagination) {
        return filterService.getAll(filter, pagination);
    }

    @Override
    public Optional<MergeRequest> getFirst(@NonNull PullRequestFilter pullRequestFilter) {
        return filterService.getFirst(pullRequestFilter);
    }

    @Override
    public boolean exists(@NonNull PullRequestFilter filter) {
        return filterService.exists(filter);
    }

    @Override
    public long count(@NonNull PullRequestFilter pullRequestFilter) {
        return filterService.count(pullRequestFilter);
    }

}
