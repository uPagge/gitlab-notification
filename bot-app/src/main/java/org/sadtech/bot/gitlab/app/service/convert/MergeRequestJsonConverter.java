package org.sadtech.bot.gitlab.app.service.convert;

import lombok.RequiredArgsConstructor;
import org.sadtech.bot.gitlab.context.domain.MergeRequestState;
import org.sadtech.bot.gitlab.context.domain.entity.MergeRequest;
import org.sadtech.bot.gitlab.sdk.domain.MergeRequestJson;
import org.sadtech.bot.gitlab.sdk.domain.MergeRequestStateJson;
import org.sadtech.haiti.context.exception.ConvertException;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * // TODO: 15.01.2021 Добавить описание.
 *
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
        mergeRequest.setLabels(source.getLabels());
        mergeRequest.setAssignee(convertPerson.convert(source.getAssignee()));
        mergeRequest.setAuthor(convertPerson.convert(source.getAssignee()));
        mergeRequest.setSourceBranch(source.getSourceBranch());
        mergeRequest.setTargetBranch(source.getTargetBranch());
        return mergeRequest;
    }

    private MergeRequestState convertState(MergeRequestStateJson state) {
        switch (state) {
            case CLOSED:
                return MergeRequestState.CLOSED;
            case LOCKED:
                return MergeRequestState.LOCKED;
            case MERGED:
                return MergeRequestState.MERGED;
            case OPENED:
                return MergeRequestState.OPENED;
        }
        throw new ConvertException("Статус ПР не найден");
    }

}
