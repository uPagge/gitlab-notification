package org.sadtech.bot.bitbucketbot.exception;

abstract class BitbucketBotException extends RuntimeException {

    protected BitbucketBotException(String message) {
        super(message);
    }

    protected BitbucketBotException(String message, Throwable throwable) {
        super(message, throwable);
    }

}
