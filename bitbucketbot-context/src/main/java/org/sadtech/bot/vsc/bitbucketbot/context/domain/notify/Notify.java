package org.sadtech.bot.vsc.bitbucketbot.context.domain.notify;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.sadtech.bot.vsc.bitbucketbot.context.domain.EntityType;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
@Setter
public abstract class Notify {

    public static final Set<Character> FORBIDDEN_SYMBOLS = Stream.of(
            '\\', '+', '`', '[', ']', '\"', '~', '*', '#', '=', '_', '>', '<'
    ).collect(Collectors.toSet());

    protected EntityType entityType;
    protected Set<String> recipients;

    protected Notify(EntityType entityType, Set<String> recipients) {
        this.entityType = entityType;
        this.recipients = recipients;
    }

    public abstract String generateMessage();

    public static String escapeMarkdown(@NonNull String s) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (FORBIDDEN_SYMBOLS.contains(c)) {
                sb.append('\\');
            }
            sb.append(c);
        }
        return sb.toString();
    }

}
