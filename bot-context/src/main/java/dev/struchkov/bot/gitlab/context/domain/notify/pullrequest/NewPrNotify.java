package dev.struchkov.bot.gitlab.context.domain.notify.pullrequest;

import dev.struchkov.bot.gitlab.context.utils.Smile;
import dev.struchkov.haiti.utils.Strings;
import lombok.Builder;
import lombok.Getter;

import java.text.MessageFormat;
import java.util.Set;
import java.util.stream.Collectors;

import static dev.struchkov.haiti.utils.Strings.escapeMarkdown;

@Getter
public class NewPrNotify extends PrNotify {

    private final String description;
    private final String author;
    private final String targetBranch;
    private final String sourceBranch;
    private final Set<String> labels;

    @Builder
    private NewPrNotify(
            String title,
            String url,
            String description,
            String author,
            String projectName,
            String targetBranch,
            String sourceBranch,
            Set<String> labels
    ) {
        super(projectName, title, url);
        this.description = description;
        this.author = author;
        this.targetBranch = targetBranch;
        this.sourceBranch = sourceBranch;
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
                "{0} *New merge request for review | {1}*{2}[{3}]({4}){5}{2}{9}: {10} {12} {11}\n{7}: {8}",
                Smile.FUN.getValue(),
                projectName,
                Smile.HR.getValue(),
                title,
                url,
                labelText,
                (description != null && !"".equals(description)) ? escapeMarkdown(description) + Smile.HR : Strings.EMPTY,
                Smile.AUTHOR.getValue(),
                author,
                Smile.TREE.getValue(),
                sourceBranch,
                targetBranch,
                Smile.ARROW.getValue()
        );
    }

}
