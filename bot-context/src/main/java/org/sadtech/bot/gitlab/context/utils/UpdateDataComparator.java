package org.sadtech.bot.gitlab.context.utils;

import org.sadtech.bot.gitlab.context.domain.entity.PullRequest;

import java.util.Comparator;

public class UpdateDataComparator implements Comparator<PullRequest> {

    @Override
    public int compare(PullRequest pullRequest, PullRequest t1) {
        return pullRequest.getUpdateDate().compareTo(t1.getUpdateDate());
    }

}
