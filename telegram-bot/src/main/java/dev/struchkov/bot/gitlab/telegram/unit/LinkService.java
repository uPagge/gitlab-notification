package dev.struchkov.bot.gitlab.telegram.unit;

import dev.struchkov.godfather.main.domain.content.Mail;
import dev.struchkov.godfather.simple.core.service.StorylineService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.ContextStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import static dev.struchkov.bot.gitlab.telegram.utils.UnitName.DISABLE_NOTIFY_MR;

@Component
@RequiredArgsConstructor
public class LinkService {

    private final StorylineService<Mail> storylineService;

    @EventListener
    public void link(ContextStartedEvent event) {
        storylineService.lazyLink(DISABLE_NOTIFY_MR, DISABLE_NOTIFY_MR);
    }

}
