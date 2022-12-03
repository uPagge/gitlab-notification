package dev.struchkov.bot.gitlab.context.domain.notify.pipeline;

import dev.struchkov.bot.gitlab.context.domain.notify.Notify;
import dev.struchkov.bot.gitlab.context.utils.Smile;
import lombok.Builder;

import java.text.MessageFormat;

import static dev.struchkov.haiti.utils.Strings.escapeMarkdown;

/**
 * @author upagge 17.01.2021
 */
public record PipelineNotify(
        Long pipelineId,
        String projectName,
        String refName,
        String oldStatus,
        String newStatus,
        String webUrl
) implements Notify {

    @Builder
    public PipelineNotify {
    }

    @Override
    public String generateMessage() {
        return MessageFormat.format(
                "{0} *Pipeline {1,number,#}* | {2}{3}[{4}]({5}){3}{6} {7} {8}",
                Smile.BUILD,
                pipelineId,
                escapeMarkdown(projectName),
                Smile.HR,
                refName,
                webUrl,
                oldStatus,
                Smile.ARROW,
                newStatus
        );
    }
}
