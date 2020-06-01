package org.sadtech.bot.bitbucketbot.controller;

import lombok.RequiredArgsConstructor;
import org.sadtech.bot.bitbucketbot.domain.entity.User;
import org.sadtech.bot.bitbucketbot.dto.UserDto;
import org.sadtech.bot.bitbucketbot.service.UserService;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final ConversionService conversionService;

    @PostMapping(value = "/api/user/reg", consumes = APPLICATION_JSON_VALUE)
    public HttpStatus register(@RequestBody UserDto userDto) {
        userService.reg(conversionService.convert(userDto, User.class));
        return HttpStatus.OK;
    }

}
