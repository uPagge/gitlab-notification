package org.sadtech.bot.bitbucketbot.service.executor;

import lombok.RequiredArgsConstructor;
import org.sadtech.bot.bitbucketbot.dto.bitbucket.CommentJson;
import org.sadtech.bot.bitbucketbot.service.Utils;

import java.security.SecureRandom;
import java.util.Optional;
import java.util.concurrent.Callable;

@RequiredArgsConstructor
public class Seeker implements Callable<Optional<ResultScan>> {

    private static final SecureRandom random = new SecureRandom();
    private final DataScan dataScan;
    private final String token;

    @Override
    public Optional<ResultScan> call() throws Exception {
//        Thread.sleep(random.nextInt(500) + 500L);
        return Utils.urlToJson(dataScan.getUrlComment(), token, CommentJson.class)
                .map(commentJson -> new ResultScan(dataScan.getUrlComment(), dataScan.getUrlPr(), commentJson));
    }

}
