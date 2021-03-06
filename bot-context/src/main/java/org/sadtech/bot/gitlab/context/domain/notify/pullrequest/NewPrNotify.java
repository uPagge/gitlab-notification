package org.sadtech.bot.gitlab.context.domain.notify.pullrequest;

import lombok.Builder;
import lombok.Getter;
import org.sadtech.bot.gitlab.context.service.AppSettingService;
import org.sadtech.bot.gitlab.context.utils.Smile;

import java.util.Set;
import java.util.stream.Collectors;

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
    public String generateMessage(AppSettingService settingService) {
        String labelText = labels.stream()
                .map(label -> "#" + label)
                .collect(Collectors.joining(" "));
        if (!labelText.isEmpty()) {
            labelText = "\n\n" + labelText;
        }
        return settingService.getMessage(
                "notify.pr.new",
                Smile.FUN.getValue(),
                projectName,
                Smile.HR.getValue(),
                title,
                url,
                labelText,
                (description != null && !"".equals(description)) ? escapeMarkdown(description) + Smile.HR : "",
                Smile.AUTHOR.getValue(),
                author,
                Smile.TREE.getValue(),
                sourceBranch,
                targetBranch,
                Smile.ARROW.getValue()
        );
    }

}
