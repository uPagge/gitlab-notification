package org.sadtech.bot.vcs.core.exception;

abstract class BitbucketBotException extends RuntimeException {

    protected BitbucketBotException(String message) {
        super(message);
    }

    protected BitbucketBotException(String message, Throwable throwable) {
        super(message, throwable);
    }

}
