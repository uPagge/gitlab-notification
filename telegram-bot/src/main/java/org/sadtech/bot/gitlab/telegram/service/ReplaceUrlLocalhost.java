package org.sadtech.bot.gitlab.telegram.service;

import org.sadtech.bot.godfather.telegram.service.SendPreProcessing;
import org.springframework.stereotype.Component;

/**
 * // TODO: 18.09.2020 Добавить описание.
 *
 * @author upagge 18.09.2020
 */
@Component
public class ReplaceUrlLocalhost implements SendPreProcessing {

    @Override
    public String pretreatment(String s) {
        return s.replace("localhost", "192.168.236.164");
    }

}
