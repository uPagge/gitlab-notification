package dev.struchkov.bot.gitlab.context.utils;

import dev.struchkov.bot.gitlab.context.domain.entity.MergeRequest;

import java.util.Comparator;

public class UpdateDataComparator implements Comparator<MergeRequest> {

    @Override
    public int compare(MergeRequest mergeRequest, MergeRequest t1) {
//        return mergeRequest.getUpdateDate().compareTo(t1.getUpdateDate());
        return 0;
    }

}
