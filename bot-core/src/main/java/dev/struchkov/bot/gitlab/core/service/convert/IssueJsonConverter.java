package dev.struchkov.bot.gitlab.core.service.convert;

import dev.struchkov.bot.gitlab.context.domain.IssueState;
import dev.struchkov.bot.gitlab.context.domain.IssueType;
import dev.struchkov.bot.gitlab.context.domain.entity.Issue;
import dev.struchkov.bot.gitlab.context.domain.entity.Person;
import dev.struchkov.bot.gitlab.sdk.domain.*;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static dev.struchkov.haiti.utils.Checker.checkNotEmpty;
import static dev.struchkov.haiti.utils.Checker.checkNotNull;

/**
 * @author Dmitry Sheyko [22.01.2023]
 */
@Component
@RequiredArgsConstructor
public class IssueJsonConverter implements Converter<IssueJson, Issue> {

    private final PersonJsonConverter convertPerson;

    @Override
    public Issue convert(IssueJson source) {
        final Issue issue = new Issue();
        issue.setId(source.getId());
        issue.setTwoId(source.getTwoId());
        issue.setProjectId(source.getProjectId());
        issue.setTitle(source.getTitle());
        issue.setDescription(source.getDescription());
        issue.setState(convertState(source.getState()));
        issue.setCreatedDate(source.getCreatedDate());
        issue.setUpdatedDate(source.getUpdatedDate());
        issue.setCloseDate(source.getClosedDate());
        issue.setType(convertType(source.getType()));
        issue.setUserNotesCount(source.getUserNotesCount());
        issue.setMergeRequestsCount(source.getMergeRequestsCount());
        issue.setUpVotes(source.getUpVotes());
        issue.setDownVotes(source.getDownVotes());
        issue.setDueDate(source.getDueDate());
        issue.setConfidential(source.getConfidential());
        issue.setDiscussionLocked(source.getDiscussionLocked());
        issue.setTaskCount(source.getTaskCompletionStatus().getCount());
        issue.setTaskCompletedCount(source.getTaskCompletionStatus().getCompletedCount());
        issue.setWebUrl(source.getWebUrl());
        issue.setBlockingIssuesCount(source.getBlockingIssuesCount());
        issue.setHasTasks(source.getHasTasks());

        convertAssignees(issue, source.getAssignees());
        convertLabels(issue, source.getLabels());

        if (checkNotNull(source.getClosedBy())) {
            issue.setClosedBy(convertPerson.convert(source.getClosedBy()));
        }

        issue.setAuthor(convertPerson.convert(source.getAuthor()));
        return issue;
    }

    private void convertAssignees(Issue issue, List<PersonJson> jsonAssignees) {
        if (checkNotEmpty(jsonAssignees)) {
            final List<Person> assignees = jsonAssignees.stream()
                    .map(convertPerson::convert)
                    .toList();
            issue.setAssignees(assignees);
        }
    }

    private void convertLabels(Issue issue, Set<String> source) {
        if (checkNotEmpty(source)) {
            final Set<String> labels = source.stream()
                    .map(label -> label.replace("-", "_"))
                    .collect(Collectors.toSet());
            issue.setLabels(labels);
        }
    }

    private IssueState convertState(IssueStateJson state) {
        return switch (state) {
            case CLOSED -> IssueState.CLOSED;
            case OPENED -> IssueState.OPENED;
        };
    }

    private IssueType convertType(IssueTypeJson type) {
        return switch (type) {
            case ISSUE -> IssueType.ISSUE;
            case INCIDENT -> IssueType.INCIDENT;
        };
    }

}