package dev.struchkov.bot.gitlab.context.domain;

import dev.struchkov.bot.gitlab.context.domain.entity.Person;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@RequiredArgsConstructor
public enum ReviewerChanged {

    BECOME(true),
    DELETED(true),
    NOT_AFFECT_USER(true),
    NOT_CHANGED(false);

    private final boolean changed;

    public static ReviewerChanged valueOf(Long gitlabUserId, List<Person> oldReviewers, List<Person> newReviewers) {
        final Map<Long, Person> oldMap = oldReviewers.stream().collect(Collectors.toMap(Person::getId, p -> p));
        final Map<Long, Person> newMap = newReviewers.stream().collect(Collectors.toMap(Person::getId, p -> p));

        if (!oldMap.keySet().equals(newMap.keySet())) {
            if (oldMap.containsKey(gitlabUserId) && !newMap.containsKey(gitlabUserId)) {
                return ReviewerChanged.DELETED;
            }
            if (!oldMap.containsKey(gitlabUserId) && newMap.containsKey(gitlabUserId)) {
                return ReviewerChanged.BECOME;
            }
            return ReviewerChanged.NOT_AFFECT_USER;
        }
        return ReviewerChanged.NOT_CHANGED;
    }

    public boolean getNewStatus(boolean oldStatus) {
        return switch (this) {
            case BECOME -> true;
            case DELETED -> false;
            case NOT_AFFECT_USER, NOT_CHANGED -> oldStatus;
        };
    }

}