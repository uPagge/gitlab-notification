package com.tsc.bitbucketbot.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDto {

    private Long telegramId;
    private String login;
    private String token;

}
