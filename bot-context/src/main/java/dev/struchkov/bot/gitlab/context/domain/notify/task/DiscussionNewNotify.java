package dev.struchkov.bot.gitlab.context.domain.notify.task;

import dev.struchkov.haiti.utils.container.Pair;
import dev.struchkov.haiti.utils.fieldconstants.annotation.FieldNames;
import lombok.Builder;
import lombok.Getter;
import lombok.Singular;

import java.util.List;

import static dev.struchkov.bot.gitlab.context.domain.notify.task.DiscussionNewNotifyFields.CLASS_NAME;

/**
 * @author upagge 10.09.2020
 */
@Getter
@FieldNames
public class DiscussionNewNotify extends TaskNotify {

    public static final String TYPE = CLASS_NAME;

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
