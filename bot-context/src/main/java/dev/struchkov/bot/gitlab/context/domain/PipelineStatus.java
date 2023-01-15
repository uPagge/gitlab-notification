package dev.struchkov.bot.gitlab.context.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author upagge 17.01.2021
 */
@Getter
@RequiredArgsConstructor
public enum PipelineStatus {

    CREATED("\uD83C\uDD95"),
    WAITING_FOR_RESOURCE("\uD83D\uDCA2"),
    PREPARING("♿️"),
    PENDING("⚠️"),
    RUNNING("\uD83D\uDD04"),
    SUCCESS("✅"),
    FAILED("❌"),
    CANCELED("\uD83D\uDEAB"),
    SKIPPED("\uD83D\uDD18"),
    MANUAL("\uD83D\uDD79"),
    SCHEDULED("\uD83D\uDD52"),
    NULL("\uD83C\uDD95");

    private final String icon;

}
