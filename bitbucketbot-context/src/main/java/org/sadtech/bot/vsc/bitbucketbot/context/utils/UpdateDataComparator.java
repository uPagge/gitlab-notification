package org.sadtech.bot.vsc.bitbucketbot.context.utils;

import org.sadtech.bot.vsc.bitbucketbot.context.domain.entity.PullRequest;

import java.util.Comparator;

public class UpdateDataComparator implements Comparator<PullRequest> {

    @Override
    public int compare(PullRequest pullRequest, PullRequest t1) {
        return pullRequest.getUpdateDate().compareTo(t1.getUpdateDate());
    }

}
