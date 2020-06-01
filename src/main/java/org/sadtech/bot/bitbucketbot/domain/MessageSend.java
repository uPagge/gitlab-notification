package org.sadtech.bot.bitbucketbot.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

@NoArgsConstructor
@Builder
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(of = "id")
public class MessageSend {

    private Long id;

    @NonNull
    private Long telegramId;

    @NonNull
    private String message;

}
