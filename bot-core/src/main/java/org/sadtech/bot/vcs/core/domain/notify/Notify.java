package org.sadtech.bot.vcs.core.domain.notify;

import lombok.Getter;
import lombok.Setter;
import org.sadtech.bot.vcs.core.domain.EntityType;

import java.util.Set;

@Getter
@Setter
public abstract class Notify {

    protected EntityType entityType;
    protected Set<String> recipients;

    protected Notify(EntityType entityType, Set<String> recipients) {
        this.entityType = entityType;
        this.recipients = recipients;
    }

    public abstract String generateMessage();

}
