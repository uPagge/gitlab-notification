package org.sadtech.bot.bitbucketbot.service;

import lombok.NonNull;
import org.sadtech.bot.bitbucketbot.domain.change.Change;

import java.util.List;

public interface ChangeService {

    void add(@NonNull Change change);

    List<Change> getNew();

}
