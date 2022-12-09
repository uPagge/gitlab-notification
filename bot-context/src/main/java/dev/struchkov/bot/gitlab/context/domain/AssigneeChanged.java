package dev.struchkov.bot.gitlab.context.domain;

import dev.struchkov.bot.gitlab.context.domain.entity.Person;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import static dev.struchkov.haiti.utils.Checker.checkNotNull;
import static dev.struchkov.haiti.utils.Checker.checkNull;

@Getter
@RequiredArgsConstructor
public enum AssigneeChanged {

    BECOME(true),
    DELETED(true),
    NOT_AFFECT_USER(true),
    NOT_CHANGED(false);

    private final boolean changed;

    public static AssigneeChanged valueOf(Long gitlabUserId, Person oldAssignee, Person newAssignee) {
        if (checkNull(oldAssignee) && checkNotNull(newAssignee) && gitlabUserId.equals(newAssignee.getId())) {
            return AssigneeChanged.BECOME;
        }
        if (checkNotNull(oldAssignee) && checkNull(newAssignee) && gitlabUserId.equals(oldAssignee.getId())) {
            return AssigneeChanged.DELETED;
        }
        if (checkNotNull(oldAssignee) && checkNotNull(newAssignee) && !oldAssignee.getId().equals(newAssignee.getId())) {
            if (gitlabUserId.equals(oldAssignee.getId())) {
                return AssigneeChanged.DELETED;
            }
            if (gitlabUserId.equals(newAssignee.getId())) {
                return AssigneeChanged.BECOME;
            }
            return AssigneeChanged.NOT_AFFECT_USER;
        }
        return AssigneeChanged.NOT_CHANGED;
    }

    public boolean getNewStatus(boolean oldStatus) {
        return switch (this) {
            case BECOME -> true;
            case DELETED -> false;
            case NOT_CHANGED, NOT_AFFECT_USER -> oldStatus;
        };
    }

}