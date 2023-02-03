package dev.struchkov.bot.gitlab.core.service.impl;

import dev.struchkov.bot.gitlab.context.domain.*;
import dev.struchkov.bot.gitlab.context.domain.entity.Issue;
import dev.struchkov.bot.gitlab.context.domain.entity.Person;
import dev.struchkov.bot.gitlab.context.domain.entity.Project;
import dev.struchkov.bot.gitlab.context.domain.notify.issue.*;
import dev.struchkov.bot.gitlab.context.repository.IssueRepository;
import dev.struchkov.bot.gitlab.context.service.IssueService;
import dev.struchkov.bot.gitlab.context.service.NotifyService;
import dev.struchkov.bot.gitlab.context.service.ProjectService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static dev.struchkov.bot.gitlab.context.domain.IssueState.CLOSED;
import static dev.struchkov.haiti.context.exception.NotFoundException.notFoundException;
import static dev.struchkov.haiti.utils.Checker.checkNotEmpty;

/**
 * @author Dmitry Sheyko [25.01.2023]
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class IssueServiceImpl implements IssueService {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    public static final Set<IssueState> DELETE_STATES = Set.of(CLOSED);

    private final PersonInformation personInformation;
    private final IssueRepository repository;
    private final ProjectService projectService;
    private final NotifyService notifyService;

    @Override
    @Transactional
    public Issue create(@NonNull Issue issue) {
        final boolean botUserAssignee = isBotUserAssignee(issue);
        final boolean botUserAssigneeAndNotAuthor = isBotUserAssigneeAndNotAuthor(issue);
        issue.setUserAssignee(botUserAssignee);
        issue.setNotification(botUserAssigneeAndNotAuthor);

        final Issue savedIssue = repository.save(issue);

        if (botUserAssigneeAndNotAuthor) {
            final String projectName = projectService.getByIdOrThrow(savedIssue.getProjectId()).getName();
            sendNotifyAboutAssignee(issue, projectName);
        }
        return savedIssue;
    }

    private boolean isBotUserAssignee(Issue savedIssue) {
        final Long gitlabUserId = personInformation.getId();
        final List<Person> assignees = savedIssue.getAssignees();

        if (checkNotEmpty(assignees)) {
            for (Person assignee : assignees) {
                if (gitlabUserId.equals(assignee.getId())) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isBotUserAssigneeAndNotAuthor(Issue savedIssue) {
        final Long gitlabUserId = personInformation.getId();
        final boolean botUserAssignee = isBotUserAssignee(savedIssue);

        if (botUserAssignee) {
            return !gitlabUserId.equals(savedIssue.getAuthor().getId());
        }
        return false;
    }

    private void sendNotifyAboutAssignee(Issue issue, String projectName) {
        final Long gitlabUserId = personInformation.getId();
        if (!gitlabUserId.equals(issue.getAuthor().getId()) // создатель Issue не является пользователем бота
        )
            notifyService.send(
                    NewIssueNotify.builder()
                            .projectName(projectName)
                            .title(issue.getTitle())
                            .url(issue.getWebUrl())
                            .issueType(issue.getType().name())
                            .author(issue.getAuthor().getName())
                            .description(issue.getDescription())
                            .dueDate(issue.getDueDate().format(DATE_FORMAT))
                            .labels(issue.getLabels())
                            .confidential(issue.getConfidential().toString())
                            .build()
            );
    }

    private void sendNotifyAboutDeleteFromAssignees(Issue issue, String projectName) {
        final Long gitlabUserId = personInformation.getId();
        if (!gitlabUserId.equals(issue.getAuthor().getId()) // создатель Issue не является пользователем бота
        )
            notifyService.send(
                    DeleteFromAssigneesNotify.builder()
                            .projectName(projectName)
                            .title(issue.getTitle())
                            .url(issue.getWebUrl())
                            .issueType(issue.getType().name())
                            .updateDate(issue.getUpdatedDate().format(DATE_FORMAT))
                            .build()
            );
    }

    @Override
    @Transactional
    public Issue update(@NonNull Issue issue) {
        final Issue oldIssue = repository.findById(issue.getId())
                .orElseThrow(notFoundException("Issue не найдено"));

        issue.setNotification(oldIssue.isNotification());
        final Long gitlabUserId = personInformation.getId();

        /**
         * проверяем изменения списка Assignees: пользователь появился в списке или удален из него или без изменений.
         */
        final AssigneesChanged assigneesChanged = AssigneesChanged.valueOf(gitlabUserId, oldIssue.getAssignees(), issue.getAssignees());
        issue.setUserAssignee(assigneesChanged.getNewStatus(oldIssue.isUserAssignee()));
        final boolean isChangedIssue = !oldIssue.getUpdatedDate().equals(issue.getUpdatedDate());

        /**
         * Удаление пользователя из assignee не всегда обновляет UpdatedDate, поэтому добавляется
         * второе условие assigneesChanged.isChanged()
         */
        if (isChangedIssue || assigneesChanged.isChanged()) {

            if (assigneesChanged.equals(AssigneesChanged.BECOME) && !gitlabUserId.equals(issue.getAuthor().getId()))
                issue.setNotification(true);

            if (issue.isNotification()) {
                final Project project = projectService.getByIdOrThrow(issue.getProjectId());
                notifyAboutStatus(oldIssue, issue, project);
                notifyAboutType(oldIssue, issue, project);
                notifyAboutTitle(oldIssue, issue, project);
                notifyAboutDescription(oldIssue, issue, project);
                notifyAboutDueDate(oldIssue, issue, project);
                notifyAboutChangeAssignees(assigneesChanged, issue, project);
            }
            return repository.save(issue);
        }
        return oldIssue;
    }

    @Override
    public ExistContainer<Issue, Long> existsById(@NonNull Set<Long> issueIds) {
        final List<Issue> existsEntity = repository.findAllById(issueIds);
        final Set<Long> existsIds = existsEntity.stream().map(Issue::getId).collect(Collectors.toSet());
        if (existsIds.containsAll(issueIds)) {
            return ExistContainer.allFind(existsEntity);
        } else {
            final Set<Long> noExistsId = issueIds.stream()
                    .filter(id -> !existsIds.contains(id))
                    .collect(Collectors.toSet());
            return ExistContainer.notAllFind(existsEntity, noExistsId);
        }
    }

    @Override
    public List<Issue> createAll(List<Issue> newIssues) {
        return newIssues.stream()
                .map(this::create)
                .toList();
    }

    @Override
    @Transactional
    public List<Issue> updateAll(@NonNull List<Issue> issues) {
        return issues.stream()
                .map(this::update)
                .collect(Collectors.toList());
    }

    @Override
    public Set<IdAndStatusIssue> getAllId(Set<IssueState> statuses) {
        return repository.findAllIdByStateIn(statuses);
    }

    protected void notifyAboutChangeAssignees(AssigneesChanged assigneesChanged, Issue issue, Project project) {
        switch (assigneesChanged) {
            case BECOME -> sendNotifyAboutAssignee(issue, project.getName());
            case DELETED -> {
                sendNotifyAboutDeleteFromAssignees(issue, project.getName());
                issue.setUserAssignee(false);
                issue.setNotification(false);
            }
        }
    }

    protected void notifyAboutTitle(Issue oldIssue, Issue newIssue, Project project) {
        final String oldTitle = oldIssue.getTitle();
        final String newTitle = newIssue.getTitle();
        final Long gitlabUserId = personInformation.getId();
        if (
                !oldTitle.equals(newTitle) // заголовок изменился
                        && !gitlabUserId.equals(oldIssue.getAuthor().getId()) // создатель Issue не является пользователем бота
        ) {
            notifyService.send(
                    TitleIssueNotify.builder()
                            .projectName(project.getName())
                            .title(oldIssue.getTitle())
                            .url(oldIssue.getWebUrl())
                            .issueType(oldIssue.getType().name())
                            .newTitle(newTitle)
                            .build()
            );
        }
    }

    protected void notifyAboutDescription(Issue oldIssue, Issue newIssue, Project project) {
        final String oldDescription = oldIssue.getDescription();
        final String newDescription = newIssue.getDescription();
        final Long gitlabUserId = personInformation.getId();
        if (
                !oldDescription.equals(newDescription) // описание изменилось
                        && !gitlabUserId.equals(oldIssue.getAuthor().getId()) // создатель Issue не является пользователем бота
        ) {
            notifyService.send(
                    DescriptionIssueNotify.builder()
                            .projectName(project.getName())
                            .title(oldIssue.getTitle())
                            .url(oldIssue.getWebUrl())
                            .issueType(oldIssue.getType().name())
                            .newDescription(newDescription)
                            .build()
            );
        }
    }

    protected void notifyAboutType(Issue oldIssue, Issue newIssue, Project project) {
        final IssueType oldType = oldIssue.getType();
        final IssueType newType = newIssue.getType();
        final Long gitlabUserId = personInformation.getId();
        if (
                !oldType.equals(newType) // тип изменился
                        && !gitlabUserId.equals(oldIssue.getAuthor().getId()) // создатель Issue не является пользователем бота
        ) {
            notifyService.send(
                    TypeIssueNotify.builder()
                            .projectName(project.getName())
                            .title(oldIssue.getTitle())
                            .url(oldIssue.getWebUrl())
                            .issueType(oldIssue.getType().name())
                            .oldType(oldType)
                            .newType(newType)
                            .build()
            );
        }
    }

    protected void notifyAboutStatus(Issue oldIssue, Issue newIssue, Project project) {
        final IssueState oldStatus = oldIssue.getState();
        final IssueState newStatus = newIssue.getState();
        final Long gitlabUserId = personInformation.getId();
        if (
                !oldStatus.equals(newStatus) // статус изменился
                        && gitlabUserId.equals(oldIssue.getAuthor().getId()) // создатель Issue является пользователем бота
        ) {
            notifyService.send(
                    StatusIssueNotify.builder()
                            .name(newIssue.getTitle())
                            .url(oldIssue.getWebUrl())
                            .issueType(oldIssue.getType().name())
                            .projectName(project.getName())
                            .newStatus(newStatus)
                            .oldStatus(oldStatus)
                            .build()
            );
        }
    }

    protected void notifyAboutDueDate(Issue oldIssue, Issue newIssue, Project project) {
        final String oldDueDate = oldIssue.getDueDate().format(DATE_FORMAT);
        final String newDueDate = newIssue.getDueDate().format(DATE_FORMAT);
        final Long gitlabUserId = personInformation.getId();
        if (
                (!Objects.equals(oldDueDate, newDueDate)) // дата изменилась
                        && (!gitlabUserId.equals(oldIssue.getAuthor().getId())) // создатель Issue не является пользователем бота
        ) {
            notifyService.send(
                    DueDateIssueNotify.builder()
                            .projectName(project.getName())
                            .title(oldIssue.getTitle())
                            .url(oldIssue.getWebUrl())
                            .issueType(oldIssue.getType().name())
                            .oldDueDate(oldDueDate)
                            .newDueDate(newDueDate)
                            .build()
            );
        }
    }

    @Override
    public void cleanOld() {
        log.debug("Старт очистки старых Issue");
        repository.deleteByStates(DELETE_STATES);
        log.debug("Конец очистки старых Issue");
    }

}