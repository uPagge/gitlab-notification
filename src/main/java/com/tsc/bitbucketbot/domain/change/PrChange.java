package com.tsc.bitbucketbot.domain.change;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.Set;

@Getter
@EqualsAndHashCode(callSuper = true)
public abstract class PrChange extends Change {

    private final String name;
    private final String url;

    protected PrChange(ChangeType type, Set<Long> telegramId, String name, String url) {
        super(type, telegramId);
        this.name = name;
        this.url = url;
    }

}
