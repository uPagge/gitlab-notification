package dev.struchkov.bot.gitlab.context.domain;

/**
 * @author upagge 17.01.2021
 */
public enum PipelineStatus {

    CREATED,
    WAITING_FOR_RESOURCE,
    PREPARING,
    PENDING,
    RUNNING,
    SUCCESS,
    FAILED,
    CANCELED,
    SKIPPED,
    MANUAL,
    SCHEDULED

}
