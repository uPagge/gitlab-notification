package org.sadtech.bot.gitlab.app.service.executor;

import lombok.Data;

@Data
public class DataScan {

    private final String urlComment;
    private final Long pullRequestId;

}
