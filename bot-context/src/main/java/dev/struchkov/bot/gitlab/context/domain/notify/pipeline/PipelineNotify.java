package dev.struchkov.bot.gitlab.context.domain.notify.pipeline;

import dev.struchkov.bot.gitlab.context.domain.PipelineStatus;
import dev.struchkov.bot.gitlab.context.domain.notify.Notify;
import lombok.Builder;
import lombok.Getter;

/**
 * @author upagge 17.01.2021
 */
//TODO [16.12.2022|uPagge]: Нужно реализовать заполнение projectName

@Getter
public final class PipelineNotify implements Notify {

    public static final String TYPE = "PipelineNotify";

    private final Long projectId;
    private final Long pipelineId;
    private final String refName;
    private final PipelineStatus oldStatus;
    private final PipelineStatus newStatus;
    private final String webUrl;

    @Builder
    public PipelineNotify(
            Long projectId,
            Long pipelineId,
            String refName,
            PipelineStatus oldStatus,
            PipelineStatus newStatus,
            String webUrl
    ) {
        this.projectId = projectId;
        this.pipelineId = pipelineId;
        this.refName = refName;
        this.oldStatus = oldStatus;
        this.newStatus = newStatus;
        this.webUrl = webUrl;
    }

    @Override
    public String getType() {
        return TYPE;
    }

}
