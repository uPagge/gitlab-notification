package org.sadtech.bot.vcs.bitbucket.sdk.domain;

import lombok.Data;

@Data
public class Properties {

    private MergeResult mergeResult;
    private Integer resolvedTaskCount = 0;
    private Integer commentCount = 0;
    private Integer openTaskCount = 0;

}
