package com.tsc.bitbucketbot.service.executor;

import com.tsc.bitbucketbot.dto.bitbucket.CommentJson;
import com.tsc.bitbucketbot.service.Utils;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class Seeker implements Callable<List<ResultScan>> {

    private final List<DataScan> dataScan;
    private final String token;

    @Override
    public List<ResultScan> call() throws Exception {
        return dataScan.stream()
                .map(
                        data -> Utils.urlToJson(data.getUrlComment(), token, CommentJson.class)
                                .map(commentJson -> new ResultScan(data.getUrlComment(), data.getUrlPr(), commentJson))
                )
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

}
