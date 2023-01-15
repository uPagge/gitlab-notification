package dev.struchkov.bot.gitlab.telegram.service.notify;

import dev.struchkov.bot.gitlab.context.domain.notify.NewProjectNotify;
import dev.struchkov.bot.gitlab.context.utils.Icons;
import dev.struchkov.godfather.main.domain.BoxAnswer;
import dev.struchkov.haiti.utils.Strings;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static dev.struchkov.bot.gitlab.context.utils.Icons.link;
import static dev.struchkov.godfather.main.domain.BoxAnswer.boxAnswer;

@Component
public class NewProjectNotifyGenerator implements NotifyBoxAnswerGenerator<NewProjectNotify> {

    @Override
    public BoxAnswer generate(NewProjectNotify notify) {
        final Optional<String> optDescription = Optional.ofNullable(notify.getProjectDescription())
                .map(Strings::escapeMarkdown);

        final StringBuilder builder = new StringBuilder(Icons.FUN).append("*New project*")
                .append(Icons.HR)
                .append(link(notify.getProjectName(), notify.getProjectUrl()))
                .append(Icons.HR);

        if (optDescription.isPresent() || !Strings.EMPTY.equals(optDescription.get())) {
            final String description = optDescription.get();
            builder.append(description)
                    .append(Icons.HR);
        }

        builder.append(Icons.AUTHOR).append(": ").append(notify.getAuthorName());

        final String notifyMessage = builder.toString();

        return boxAnswer(notifyMessage);
    }

    @Override
    public String getNotifyType() {
        return NewProjectNotify.TYPE;
    }

}
