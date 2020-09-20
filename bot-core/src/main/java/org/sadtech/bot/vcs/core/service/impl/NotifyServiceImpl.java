package org.sadtech.bot.vcs.core.service.impl;

import lombok.RequiredArgsConstructor;
import org.sadtech.bot.vcs.core.domain.notify.Notify;
import org.sadtech.bot.vcs.core.repository.NotifyRepository;
import org.sadtech.bot.vcs.core.service.NotifyService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotifyServiceImpl implements NotifyService {

    private final NotifyRepository notifyRepository;

    @Override
    public <T extends Notify> void save(T notify) {
        notifyRepository.add(notify);
    }

    @Override
    public List<Notify> getNew() {
        final List<Notify> notifies = notifyRepository.getAll();
        notifyRepository.deleteAll(notifies);
        return notifies;
    }

}
