package org.sadtech.bot.vcs.core.domain.change;

public enum ChangeType {

    STATUS_PR,
    UPDATE_PR,
    REVIEWERS,
    NEW_PR,
    CONFLICT_PR,
    NEW_COMMENT,
    NEW_ANSWERS_COMMENT,
    NEW_TASK,
    DELETED_TASK,
    RESOLVED_TASK,
    OPEN_TASK;

}
