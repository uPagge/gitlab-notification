package org.sadtech.bot.vcs.core.service.impl;

import lombok.RequiredArgsConstructor;
import org.sadtech.bot.vcs.core.domain.change.Change;
import org.sadtech.bot.vcs.core.repository.ChangeRepository;
import org.sadtech.bot.vcs.core.service.ChangeService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChangeServiceImpl implements ChangeService {

    private final ChangeRepository changeRepository;

    @Override
    public <T extends Change> void save(T change) {
        changeRepository.add(change);
    }

    @Override
    public List<Change> getNew() {
        final List<Change> changes = changeRepository.getAll();
        changeRepository.deleteAll(changes);
        return changes;
    }

}
