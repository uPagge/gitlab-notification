package dev.struchkov.bot.gitlab.context.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "deferred_messages")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class DeferredMessage {
    @Id
    @Column(name = "id")
    private int id;
    @Column(name = "message")
    private String message;
    @Column (name = "time")
    private LocalDateTime time;
}
