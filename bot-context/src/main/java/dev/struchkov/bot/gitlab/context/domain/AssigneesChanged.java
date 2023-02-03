package dev.struchkov.bot.gitlab.context.domain;

import dev.struchkov.bot.gitlab.context.domain.entity.Person;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Dmitry Sheyko [25.01.2023]
 */
@Getter
@RequiredArgsConstructor
public enum AssigneesChanged {

    BECOME(true),
    DELETED(true),
    NOT_AFFECT_USER(true),
    NOT_CHANGED(false);

    private final boolean changed;

    public static AssigneesChanged valueOf(Long gitlabUserId, List<Person> oldAssignees, List<Person> newAssignees) {
        final Map<Long, Person> oldMap = oldAssignees.stream().collect(Collectors.toMap(Person::getId, p -> p));
        final Map<Long, Person> newMap = newAssignees.stream().collect(Collectors.toMap(Person::getId, p -> p));

        if (!oldMap.keySet().equals(newMap.keySet())) {

            if (oldMap.containsKey(gitlabUserId) && !newMap.containsKey(gitlabUserId)) {
                return AssigneesChanged.DELETED;
            }
            if (!oldMap.containsKey(gitlabUserId) && newMap.containsKey(gitlabUserId)) {
                return AssigneesChanged.BECOME;
            }
            return AssigneesChanged.NOT_AFFECT_USER;
        }
        return AssigneesChanged.NOT_CHANGED;
    }

    public boolean getNewStatus(boolean oldStatus) {
        return switch (this) {
            case BECOME -> true;
            case DELETED -> false;
            case NOT_AFFECT_USER, NOT_CHANGED -> oldStatus;
        };
    }

}