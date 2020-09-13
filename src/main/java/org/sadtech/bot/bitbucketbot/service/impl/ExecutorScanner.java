package org.sadtech.bot.bitbucketbot.service.impl;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sadtech.bot.bitbucketbot.config.properties.BitbucketProperty;
import org.sadtech.bot.bitbucketbot.dto.bitbucket.CommentJson;
import org.sadtech.bot.bitbucketbot.service.executor.DataScan;
import org.sadtech.bot.bitbucketbot.service.executor.Executor;
import org.sadtech.bot.bitbucketbot.service.executor.Seeker;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExecutorScanner implements Executor<DataScan, CommentJson> {

    private final ExecutorService executorService;
    private List<Future<Optional<CommentJson>>> resultList = new ArrayList<>();
    private final BitbucketProperty bitbucketProperty;

    @Override
    public boolean registration(@NonNull List<DataScan> dataScans) {
        resultList.addAll(
                dataScans.stream()
                        .map(dataScan -> new Seeker(dataScan, bitbucketProperty.getToken()))
                        .map(executorService::submit)
                        .collect(Collectors.toList())
        );
        return true;
    }

    @Override
    public List<CommentJson> getResult() {
        while (!resultList.stream().allMatch(Future::isDone)) {

        }
        final List<CommentJson> result = resultList.stream()
                .filter(Future::isDone)
                .map(this::getResultScan)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
        resultList.clear();
        return result;
    }

    private Optional<CommentJson> getResultScan(Future<Optional<CommentJson>> test) {
        try {
            return test.get();
        } catch (InterruptedException | ExecutionException e) {
            log.error(e.getMessage());
        }
        return Optional.empty();
    }

}
