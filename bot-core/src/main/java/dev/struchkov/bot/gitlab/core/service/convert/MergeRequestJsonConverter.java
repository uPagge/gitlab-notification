package dev.struchkov.bot.gitlab.core.service.convert;

import dev.struchkov.bot.gitlab.context.domain.MergeRequestState;
import dev.struchkov.bot.gitlab.context.domain.entity.MergeRequest;
import dev.struchkov.bot.gitlab.context.domain.entity.Person;
import dev.struchkov.bot.gitlab.sdk.domain.MergeRequestJson;
import dev.struchkov.bot.gitlab.sdk.domain.MergeRequestStateJson;
import dev.struchkov.bot.gitlab.sdk.domain.PersonJson;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static dev.struchkov.haiti.utils.Checker.checkNotEmpty;
import static dev.struchkov.haiti.utils.Checker.checkNotNull;

/**
 * @author upagge 15.01.2021
 */
@Component
@RequiredArgsConstructor
public class MergeRequestJsonConverter implements Converter<MergeRequestJson, MergeRequest> {

    private final PersonJsonConverter convertPerson;

    @Override
    public MergeRequest convert(MergeRequestJson source) {
        final MergeRequest mergeRequest = new MergeRequest();
        mergeRequest.setConflict(source.isConflicts());
        mergeRequest.setTitle(source.getTitle());
        mergeRequest.setCreatedDate(source.getCreatedDate());
        mergeRequest.setDescription(source.getDescription());
        mergeRequest.setId(source.getId());
        mergeRequest.setTwoId(source.getTwoId());
        mergeRequest.setUpdatedDate(source.getUpdatedDate());
        mergeRequest.setState(convertState(source.getState()));
        mergeRequest.setProjectId(source.getProjectId());
        mergeRequest.setWebUrl(source.getWebUrl());

        convertLabels(mergeRequest, source.getLabels());
        convertReviewers(mergeRequest, source.getReviewers());

        if (checkNotNull(source.getAssignee())) {
            mergeRequest.setAssignee(convertPerson.convert(source.getAssignee()));
        }

        mergeRequest.setAuthor(convertPerson.convert(source.getAuthor()));
        mergeRequest.setSourceBranch(source.getSourceBranch());
        mergeRequest.setTargetBranch(source.getTargetBranch());
        return mergeRequest;
    }

    private void convertReviewers(MergeRequest mergeRequest, List<PersonJson> jsonReviewers) {
        if (checkNotEmpty(jsonReviewers)) {
            final List<Person> reviewers = jsonReviewers.stream()
                    .map(convertPerson::convert)
                    .toList();
            mergeRequest.setReviewers(reviewers);
        }
    }

    private static void convertLabels(MergeRequest mergeRequest, Set<String> source) {
        if (checkNotEmpty(source)) {
            final Set<String> labels = source.stream()
                    .map(label -> label.replace("-", "_"))
                    .collect(Collectors.toSet());
            mergeRequest.setLabels(labels);
        }
    }

    private MergeRequestState convertState(MergeRequestStateJson state) {
        return switch (state) {
            case CLOSED -> MergeRequestState.CLOSED;
            case LOCKED -> MergeRequestState.LOCKED;
            case MERGED -> MergeRequestState.MERGED;
            case OPENED -> MergeRequestState.OPENED;
        };
    }

}
