package org.sadtech.bot.vcs.core.domain.notify;

import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public abstract class Notify {

    protected TypeNotify typeNotify;
    protected Set<String> logins;

    protected Notify(Set<String> logins) {
        this.typeNotify = TypeNotify.PERSON;
        this.logins = logins;
    }

    public abstract String generateMessage();

}
