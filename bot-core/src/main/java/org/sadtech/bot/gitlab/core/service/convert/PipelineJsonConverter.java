package org.sadtech.bot.gitlab.core.service.convert;

import lombok.RequiredArgsConstructor;
import org.sadtech.bot.gitlab.context.domain.PipelineStatus;
import org.sadtech.bot.gitlab.context.domain.entity.Pipeline;
import org.sadtech.bot.gitlab.sdk.domain.PipelineJson;
import org.sadtech.bot.gitlab.sdk.domain.PipelineStatusJson;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import static org.sadtech.bot.gitlab.context.domain.PipelineStatus.CANCELED;
import static org.sadtech.bot.gitlab.context.domain.PipelineStatus.CREATED;
import static org.sadtech.bot.gitlab.context.domain.PipelineStatus.FAILED;
import static org.sadtech.bot.gitlab.context.domain.PipelineStatus.MANUAL;
import static org.sadtech.bot.gitlab.context.domain.PipelineStatus.PENDING;
import static org.sadtech.bot.gitlab.context.domain.PipelineStatus.PREPARING;
import static org.sadtech.bot.gitlab.context.domain.PipelineStatus.RUNNING;
import static org.sadtech.bot.gitlab.context.domain.PipelineStatus.SCHEDULED;
import static org.sadtech.bot.gitlab.context.domain.PipelineStatus.SKIPPED;
import static org.sadtech.bot.gitlab.context.domain.PipelineStatus.SUCCESS;
import static org.sadtech.bot.gitlab.context.domain.PipelineStatus.WAITING_FOR_RESOURCE;

/**
 * // TODO: 17.01.2021 Добавить описание.
 *
 * @author upagge 17.01.2021
 */
@Component
@RequiredArgsConstructor
public class PipelineJsonConverter implements Converter<PipelineJson, Pipeline> {

    private final PersonJsonConverter convertPerson;

    @Override
    public Pipeline convert(PipelineJson source) {
        final Pipeline pipeline = new Pipeline();
        pipeline.setId(source.getId());
        pipeline.setCreated(source.getCreated());
        pipeline.setUpdated(source.getUpdated());
        pipeline.setRef(source.getRef());
        pipeline.setWebUrl(source.getWebUrl());
        pipeline.setStatus(convertStatus(source.getStatus()));
        pipeline.setPerson(convertPerson.convert(source.getUser()));
        return pipeline;
    }

    private PipelineStatus convertStatus(PipelineStatusJson status) {
        switch (status) {
            case SKIPPED:
                return SKIPPED;
            case CANCELED:
                return CANCELED;
            case SUCCESS:
                return SUCCESS;
            case MANUAL:
                return MANUAL;
            case CREATED:
                return CREATED;
            case PENDING:
                return PENDING;
            case RUNNING:
                return RUNNING;
            case PREPARING:
                return PREPARING;
            case SCHEDULED:
                return SCHEDULED;
            case WAITING_FOR_RESOURCE:
                return WAITING_FOR_RESOURCE;
            default:
                return FAILED;
        }
    }

}
