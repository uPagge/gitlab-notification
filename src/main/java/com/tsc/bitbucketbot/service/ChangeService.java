package com.tsc.bitbucketbot.service;

import com.tsc.bitbucketbot.domain.change.Change;
import lombok.NonNull;

import java.util.List;

public interface ChangeService {

    void add(@NonNull Change change);

    List<Change> getNew();

}
