package org.sadtech.bot.vsc.bitbucketbot.context.exception;

abstract class BitbucketBotException extends RuntimeException {

    public BitbucketBotException(String message) {
        super(message);
    }

    public BitbucketBotException(String message, Throwable throwable) {
        super(message, throwable);
    }

}
