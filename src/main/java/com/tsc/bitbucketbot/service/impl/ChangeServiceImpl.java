package com.tsc.bitbucketbot.service.impl;

import com.tsc.bitbucketbot.domain.change.Change;
import com.tsc.bitbucketbot.repository.ChangeRepository;
import com.tsc.bitbucketbot.service.ChangeService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
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
