package org.sadtech.bot.vcs.core.utils;

import org.sadtech.bot.vcs.core.domain.entity.PullRequest;

import java.util.Comparator;

public class UpdateDataComparator implements Comparator<PullRequest> {

    @Override
    public int compare(PullRequest pullRequest, PullRequest t1) {
        return pullRequest.getUpdateDate().compareTo(t1.getUpdateDate());
    }

}
