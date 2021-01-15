package org.sadtech.bot.gitlab.context.domain.notify.pullrequest;

import lombok.Builder;
import lombok.Getter;
import org.sadtech.bot.gitlab.context.utils.Smile;

import java.text.MessageFormat;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
public class NewPrNotify extends PrNotify {

    private final String description;
    private final String author;
    private final Set<String> labels;

    @Builder
    private NewPrNotify(
            String title,
            String url,
            String description,
            String author,
            String projectName,
            Set<String> labels) {
        super(projectName, title, url);
        this.description = description;
        this.author = author;
        this.labels = labels;
    }

    @Override
    public String generateMessage() {
        String labelText = labels.stream()
                .map(label -> "#" + label)
                .collect(Collectors.joining(" "));
        if (!labelText.isEmpty()) {
            labelText = "\n\n" + labelText;
        }
        return MessageFormat.format(
                "{0} *Новый PullRequest | {1}*{2}" +
                        "[{3}]({4})" +
                        "{5}" +
                        "{2}{7}: {8}\n\n",
                Smile.FUN, projectName, Smile.HR, title, url, labelText,
                (description != null && !"".equals(description)) ? escapeMarkdown(description) + Smile.HR : "",
                Smile.AUTHOR, author
        );
    }

}
