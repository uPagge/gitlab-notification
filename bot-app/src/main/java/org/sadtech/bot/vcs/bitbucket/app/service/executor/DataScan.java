package org.sadtech.bot.vcs.bitbucket.app.service.executor;

import lombok.Data;

@Data
public class DataScan {

    private final String urlComment;
    private final Long pullRequestId;

}
