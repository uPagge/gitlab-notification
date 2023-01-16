package dev.struchkov.bot.gitlab.telegram.service.notify;

import dev.struchkov.bot.gitlab.context.domain.notify.mergerequest.ConflictMrNotify;
import dev.struchkov.bot.gitlab.context.utils.Icons;
import dev.struchkov.godfather.main.domain.BoxAnswer;
import org.springframework.stereotype.Component;

import static dev.struchkov.bot.gitlab.context.utils.Icons.link;
import static dev.struchkov.godfather.main.domain.BoxAnswer.boxAnswer;
import static dev.struchkov.haiti.utils.Strings.escapeMarkdown;

@Component
public class ConflictPrNotifyGenerator implements NotifyBoxAnswerGenerator<ConflictMrNotify> {

    @Override
    public BoxAnswer generate(ConflictMrNotify notify) {

        final StringBuilder builder = new StringBuilder(Icons.DANGEROUS).append(" *Attention! MergeRequest conflict | ").append(escapeMarkdown(notify.getProjectName())).append("*")
                .append(Icons.HR)
                .append(link(notify.getTitle(), notify.getUrl()));

        final String notifyMessage = builder.toString();
        return boxAnswer(notifyMessage);
    }

    @Override
    public String getNotifyType() {
        return ConflictMrNotify.TYPE;
    }

}
