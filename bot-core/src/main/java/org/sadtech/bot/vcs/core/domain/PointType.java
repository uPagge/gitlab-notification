package org.sadtech.bot.vcs.core.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * // TODO: 01.10.2020 Добавить описание.
 *
 * @author upagge 01.10.2020
 */
@Getter
@RequiredArgsConstructor
public enum PointType {

    CREATE_PULL_REQUEST(10),
    DECLINE_PULL_REQUEST(-CREATE_PULL_REQUEST.getPoints()),
    COMMENT_ADD(1),
    COMMENT_DELETE(-COMMENT_ADD.getPoints()),
    TASK_CREATE(2),
    TASK_DELETE(-TASK_CREATE.getPoints()),
    TASK_RECIPIENT(-1),
    TASK_DELETE_RECIPIENT(1);

    private final Integer points;

}
