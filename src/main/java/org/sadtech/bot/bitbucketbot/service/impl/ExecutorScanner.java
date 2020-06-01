package org.sadtech.bot.bitbucketbot.service.impl;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sadtech.bot.bitbucketbot.config.BitbucketConfig;
import org.sadtech.bot.bitbucketbot.service.executor.DataScan;
import org.sadtech.bot.bitbucketbot.service.executor.Executor;
import org.sadtech.bot.bitbucketbot.service.executor.ResultScan;
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
public class ExecutorScanner implements Executor<DataScan, ResultScan> {

    private final ExecutorService executorService;
    private final List<Future<Optional<ResultScan>>> resultList = new ArrayList<>();
    private final BitbucketConfig bitbucketConfig;

    @Override
    public boolean registration(@NonNull List<DataScan> dataScans) {
        dataScans.stream()
                .map(dataScan -> new Seeker(dataScan, bitbucketConfig.getToken()))
                .forEach(seeker -> executorService.submit(seeker));
        return true;
    }

    @Override
    public List<ResultScan> getResult() {
        while (!resultList.stream().allMatch(Future::isDone)) {

        }
        final List<ResultScan> result = resultList.stream()
                .filter(Future::isDone)
                .map(this::getResultScan)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
        resultList.clear();
        return result;
    }

    private Optional<ResultScan> getResultScan(Future<Optional<ResultScan>> test) {
        try {
            return test.get();
        } catch (InterruptedException | ExecutionException e) {
            log.error(e.getMessage());
        }
        return Optional.empty();
    }

}
