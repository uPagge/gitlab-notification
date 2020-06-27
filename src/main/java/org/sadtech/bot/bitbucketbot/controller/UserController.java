package org.sadtech.bot.bitbucketbot.controller;

import lombok.RequiredArgsConstructor;
import org.sadtech.bot.bitbucketbot.domain.entity.Person;
import org.sadtech.bot.bitbucketbot.dto.UserDto;
import org.sadtech.bot.bitbucketbot.service.PersonService;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * Контроллер отвечат за регистрацию пользователей.
 *
 * @author upagge
 */
@RestController
@RequiredArgsConstructor
public class UserController {

    private final PersonService personService;
    private final ConversionService conversionService;

    @PostMapping(value = "/api/user/reg", consumes = APPLICATION_JSON_VALUE)
    public HttpStatus register(@RequestBody UserDto userDto) {
        personService.reg(conversionService.convert(userDto, Person.class));
        return HttpStatus.OK;
    }

}
