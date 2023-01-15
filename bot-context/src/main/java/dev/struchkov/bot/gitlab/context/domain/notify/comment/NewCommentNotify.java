package dev.struchkov.bot.gitlab.context.domain.notify.comment;

import dev.struchkov.bot.gitlab.context.domain.notify.Notify;
import lombok.Builder;
import lombok.Getter;

@Getter
public final class NewCommentNotify implements Notify {

    public static final String TYPE = "NewCommentNotify";

    private final String url;
    private final String discussionMessage;
    private final String discussionAuthor;
    private final String previousMessage;
    private final String previousAuthor;
    private final String authorName;
    private final String message;

    @Builder
    public NewCommentNotify(
            String url,
            String discussionMessage,
            String discussionAuthor,
            String previousMessage,
            String previousAuthor,
            String authorName,
            String message
    ) {
        this.url = url;
        this.discussionMessage = discussionMessage;
        this.discussionAuthor = discussionAuthor;
        this.previousMessage = previousMessage;
        this.previousAuthor = previousAuthor;
        this.authorName = authorName;
        this.message = message;
    }


    @Override
    public String getType() {
        return TYPE;
    }

}


