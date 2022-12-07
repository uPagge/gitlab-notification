package dev.struchkov.bot.gitlab.context.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class MessageSend {

    @EqualsAndHashCode.Include
    private Long id;
    private Long telegramId;
    private String message;

}
