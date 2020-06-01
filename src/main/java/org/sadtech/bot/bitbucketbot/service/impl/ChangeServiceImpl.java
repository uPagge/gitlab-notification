package org.sadtech.bot.bitbucketbot.service.impl;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.sadtech.bot.bitbucketbot.domain.change.Change;
import org.sadtech.bot.bitbucketbot.repository.ChangeRepository;
import org.sadtech.bot.bitbucketbot.service.ChangeService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChangeServiceImpl implements ChangeService {

    private final ChangeRepository changeRepository;

    @Override
    public void add(@NonNull Change change) {
        changeRepository.add(change);
    }

    @Override
    public List<Change> getNew() {
        final List<Change> changes = changeRepository.getAll();
        changeRepository.deleteAll(changes);
        return changes;
    }


}
