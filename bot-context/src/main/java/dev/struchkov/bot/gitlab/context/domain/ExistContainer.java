package dev.struchkov.bot.gitlab.context.domain;

import lombok.NonNull;

import java.util.Collections;
import java.util.List;
import java.util.Set;

public class ExistContainer<Entity, Key> {

    protected final List<Entity> container;
    protected final boolean allFound;
    protected final Set<Key> idNoFound;

    protected ExistContainer(List<Entity> container, boolean allFound, Set<Key> idNoFound) {
        this.container = container;
        this.allFound = allFound;
        this.idNoFound = idNoFound;
    }

    public static <T, K> ExistContainer<T, K> allFind(@NonNull List<T> container) {
        return new ExistContainer<>(container, true, Collections.emptySet());
    }

    public static <T, K> ExistContainer<T, K> notAllFind(@NonNull List<T> container, @NonNull Set<K> idNoFound) {
        return new ExistContainer<>(container, false, idNoFound);
    }

    public List<Entity> getContainer() {
        return container;
    }

    public boolean isAllFound() {
        return allFound;
    }

    public Set<Key> getIdNoFound() {
        return idNoFound;
    }

}
