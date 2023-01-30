package dev.struchkov.bot.gitlab.data.jpa;

import dev.struchkov.bot.gitlab.context.domain.entity.DeferredMessage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DelayedNotifyJpaRepository extends JpaRepository<DeferredMessage, Long> {
}
