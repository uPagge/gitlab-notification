package dev.struchkov.bot.gitlab.context.exception;

import dev.struchkov.haiti.context.exception.BasicException;

abstract class BitbucketBotException extends BasicException {

    public BitbucketBotException(String message) {
        super(message);
    }

    public BitbucketBotException(String message, Throwable throwable) {
        super(message, throwable);
    }

}
