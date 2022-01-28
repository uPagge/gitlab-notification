package dev.struchkov.bot.gitlab.context.exception;

import dev.struchkov.haiti.context.exception.BasicException;

abstract class GitlabBotException extends BasicException {

    public GitlabBotException(String message) {
        super(message);
    }

    public GitlabBotException(String message, Throwable throwable) {
        super(message, throwable);
    }

}
