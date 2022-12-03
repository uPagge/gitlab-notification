package dev.struchkov.bot.gitlab.core.service.convert;

import dev.struchkov.bot.gitlab.context.domain.MergeRequestState;
import dev.struchkov.bot.gitlab.context.domain.entity.MergeRequest;
import dev.struchkov.bot.gitlab.sdk.domain.MergeRequestJson;
import dev.struchkov.bot.gitlab.sdk.domain.MergeRequestStateJson;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

import static dev.struchkov.haiti.utils.Checker.checkNotEmpty;

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
        mergeRequest.setLabels(convertLabels(source.getLabels()));
        if (source.getAssignee() != null) {
            mergeRequest.setAssignee(convertPerson.convert(source.getAssignee()));
        }
        mergeRequest.setAuthor(convertPerson.convert(source.getAuthor()));
        mergeRequest.setSourceBranch(source.getSourceBranch());
        mergeRequest.setTargetBranch(source.getTargetBranch());
        return mergeRequest;
    }

    private static Set<String> convertLabels(Set<String> source) {
        if (checkNotEmpty(source)) {
            return source.stream()
                    .map(label -> label.replaceAll("-", "_"))
                    .collect(Collectors.toSet());
        }
        return null;
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
