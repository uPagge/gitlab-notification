package org.sadtech.bot.vcs.core.domain.notify;

import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public abstract class Notify {

    protected Set<String> logins;

    protected Notify(Set<String> logins) {
        this.logins = logins;
    }

    public abstract String generateMessage();

}
