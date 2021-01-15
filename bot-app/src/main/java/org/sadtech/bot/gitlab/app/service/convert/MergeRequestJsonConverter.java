package org.sadtech.bot.gitlab.app.service.convert;

import org.sadtech.bot.gitlab.context.domain.MergeRequestState;
import org.sadtech.bot.gitlab.context.domain.entity.MergeRequest;
import org.sadtech.bot.gitlab.context.domain.entity.Person;
import org.sadtech.bot.gitlab.sdk.domain.MergeRequestJson;
import org.sadtech.bot.gitlab.sdk.domain.MergeRequestStateJson;
import org.sadtech.bot.gitlab.sdk.domain.PersonJson;
import org.sadtech.haiti.context.exception.ConvertException;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * // TODO: 15.01.2021 Добавить описание.
 *
 * @author upagge 15.01.2021
 */
@Component
public class MergeRequestJsonConverter implements Converter<MergeRequestJson, MergeRequest> {

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
        mergeRequest.setAssignee(convertPerson(source.getAssignee()));
        mergeRequest.setAuthor(convertPerson(source.getAssignee()));
        return mergeRequest;
    }

    private Person convertPerson(PersonJson personJson) {
        final Person person = new Person();
        person.setId(personJson.getId());
        person.setName(personJson.getName());
        person.setUserName(personJson.getUsername());
        person.setWebUrl(personJson.getWebUrl());
        return person;
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
