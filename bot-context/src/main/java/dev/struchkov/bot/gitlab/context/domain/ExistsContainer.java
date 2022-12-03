package dev.struchkov.bot.gitlab.context.domain;

import lombok.NonNull;

import java.util.Collection;
import java.util.Collections;

public class ExistsContainer<Entity, Key> {

    protected final Collection<Entity> container;
    protected final boolean allFound;
    protected final Collection<Key> idNoFound;

    protected ExistsContainer(Collection<Entity> container, boolean allFound, Collection<Key> idNoFound) {
        this.container = container;
        this.allFound = allFound;
        this.idNoFound = idNoFound;
    }

    public static <T, K> ExistsContainer<T, K> allFind(@NonNull Collection<T> container) {
        return new ExistsContainer<>(container, true, Collections.emptyList());
    }

    public static <T, K> ExistsContainer<T, K> notAllFind(@NonNull Collection<T> container, @NonNull Collection<K> idNoFound) {
        return new ExistsContainer<>(container, false, idNoFound);
    }

    public Collection<Entity> getContainer() {
        return container;
    }

    public boolean isAllFound() {
        return allFound;
    }

    public Collection<Key> getIdNoFound() {
        return idNoFound;
    }

}
