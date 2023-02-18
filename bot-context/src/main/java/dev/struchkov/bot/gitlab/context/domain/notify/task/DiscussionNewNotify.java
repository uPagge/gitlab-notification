package dev.struchkov.bot.gitlab.context.domain.notify.task;

import dev.struchkov.haiti.utils.container.Pair;
import lombok.Builder;
import lombok.Getter;
import lombok.Singular;

import java.util.List;

/**
 * @author upagge 10.09.2020
 */
@Getter
public class DiscussionNewNotify extends TaskNotify {

    public static final String TYPE = "DiscussionNewNotify";

    private final String threadId;
    private final String mrName;
    private final List<Pair<String, String>> notes;

    @Builder
    public DiscussionNewNotify(
            String threadId,
            String mrName,
            String authorName,
            String url,
            String discussionMessage,
            @Singular List<Pair<String, String>> notes
    ) {
        super(authorName, url, discussionMessage);
        this.threadId = threadId;
        this.mrName = mrName;
        this.notes = notes;
    }

    @Override
    public String getType() {
        return TYPE;
    }

}
