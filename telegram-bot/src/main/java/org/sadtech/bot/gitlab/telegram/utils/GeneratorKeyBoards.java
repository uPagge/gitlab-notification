package org.sadtech.bot.gitlab.telegram.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.sadtech.social.core.domain.keyboard.KeyBoard;
import org.sadtech.social.core.domain.keyboard.KeyBoardLine;
import org.sadtech.social.core.domain.keyboard.button.KeyBoardButtonText;

/**
 * // TODO: 20.09.2020 Добавить описание.
 *
 * @author upagge 20.09.2020
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GeneratorKeyBoards {

    public static KeyBoard menu() {
        final KeyBoardButtonText tasks = KeyBoardButtonText.builder().label("Мои задачи").build();
        final KeyBoardButtonText pr = KeyBoardButtonText.builder().label("Pull Requests").build();
        final KeyBoardButtonText top = KeyBoardButtonText.builder().label("\uD83C\uDF1F Таблица рейтинга \uD83C\uDF1F").build();
        final KeyBoardButtonText settings = KeyBoardButtonText.builder().label("Настройки").build();

        final KeyBoardLine oneLine = KeyBoardLine.builder()
                .buttonKeyBoard(tasks)
                .buttonKeyBoard(pr)
                .build();

        final KeyBoardLine twoLine = KeyBoardLine.builder()
                .buttonKeyBoard(top)
                .build();

        final KeyBoardLine threeLine = KeyBoardLine.builder()
                .buttonKeyBoard(settings)
                .build();

        return KeyBoard.builder()
                .lineKeyBoard(oneLine)
                .lineKeyBoard(twoLine)
                .lineKeyBoard(threeLine)
                .build();
    }

}
