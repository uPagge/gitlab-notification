package org.sadtech.bot.bitbucketbot.domain.change;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public abstract class Change {

    protected final ChangeType type;
    protected final LocalDateTime localDateTime = LocalDateTime.now();
    protected final Set<Long> telegramIds;

    @Setter
    @EqualsAndHashCode.Include
    protected Long id;

    protected Change(ChangeType type, Set<Long> telegramIds) {
        this.type = type;
        this.telegramIds = telegramIds;
    }

    public abstract String generateMessage();

}
