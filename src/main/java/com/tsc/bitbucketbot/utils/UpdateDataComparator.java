package com.tsc.bitbucketbot.utils;

import com.tsc.bitbucketbot.domain.entity.PullRequest;

import java.util.Comparator;

public class UpdateDataComparator implements Comparator<PullRequest> {

    @Override
    public int compare(PullRequest pullRequest, PullRequest t1) {
        return pullRequest.getUpdateDate().compareTo(t1.getUpdateDate());
    }

}
