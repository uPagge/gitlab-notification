package com.tsc.bitbucketbot.service.impl;

import com.google.common.collect.Lists;
import com.tsc.bitbucketbot.config.BitbucketConfig;
import com.tsc.bitbucketbot.service.executor.DataScan;
import com.tsc.bitbucketbot.service.executor.Executor;
import com.tsc.bitbucketbot.service.executor.ResultScan;
import com.tsc.bitbucketbot.service.executor.Seeker;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExecutorScanner implements Executor<DataScan, ResultScan> {

    private final ExecutorService executorService;
    private final List<Future<List<ResultScan>>> resultList = new ArrayList<>();
    private final BitbucketConfig bitbucketConfig;

    @Override
    public boolean registration(@NonNull List<DataScan> dataScans) {
        Lists.partition(dataScans, 20).forEach(
                list -> resultList.add(executorService.submit(new Seeker(list, bitbucketConfig.getToken())))
        );
        return true;
    }

    @Override
    public List<ResultScan> getResult() {
        while (!resultList.stream().allMatch(Future::isDone)) {

        }
        final List<ResultScan> result = resultList.stream()
                .filter(Future::isDone)
                .map(this::getResultScans)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
        resultList.clear();
        return result;
    }

    private List<ResultScan> getResultScans(Future<List<ResultScan>> listFuture) {
        try {
            return listFuture.get();
        } catch (InterruptedException | ExecutionException e) {
            log.error(e.getMessage());
        }
        return Collections.emptyList();
    }


}
