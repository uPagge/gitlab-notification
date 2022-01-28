package dev.struchkov.bot.gitlab.core.service.convert;

import dev.struchkov.bot.gitlab.context.domain.PipelineStatus;
import dev.struchkov.bot.gitlab.context.domain.entity.Pipeline;
import dev.struchkov.bot.gitlab.sdk.domain.PipelineJson;
import dev.struchkov.bot.gitlab.sdk.domain.PipelineStatusJson;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import static dev.struchkov.bot.gitlab.context.domain.PipelineStatus.CANCELED;
import static dev.struchkov.bot.gitlab.context.domain.PipelineStatus.CREATED;
import static dev.struchkov.bot.gitlab.context.domain.PipelineStatus.FAILED;
import static dev.struchkov.bot.gitlab.context.domain.PipelineStatus.MANUAL;
import static dev.struchkov.bot.gitlab.context.domain.PipelineStatus.PENDING;
import static dev.struchkov.bot.gitlab.context.domain.PipelineStatus.PREPARING;
import static dev.struchkov.bot.gitlab.context.domain.PipelineStatus.RUNNING;
import static dev.struchkov.bot.gitlab.context.domain.PipelineStatus.SCHEDULED;
import static dev.struchkov.bot.gitlab.context.domain.PipelineStatus.SKIPPED;
import static dev.struchkov.bot.gitlab.context.domain.PipelineStatus.SUCCESS;
import static dev.struchkov.bot.gitlab.context.domain.PipelineStatus.WAITING_FOR_RESOURCE;

/**
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
        return switch (status) {
            case SKIPPED -> SKIPPED;
            case CANCELED -> CANCELED;
            case SUCCESS -> SUCCESS;
            case MANUAL -> MANUAL;
            case CREATED -> CREATED;
            case PENDING -> PENDING;
            case RUNNING -> RUNNING;
            case PREPARING -> PREPARING;
            case SCHEDULED -> SCHEDULED;
            case WAITING_FOR_RESOURCE -> WAITING_FOR_RESOURCE;
            default -> FAILED;
        };
    }

}
