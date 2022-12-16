package dev.struchkov.bot.gitlab.core.utils;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinTask;

@Slf4j
@UtilityClass
public class PoolUtils {

    public static <T> List<T> pullTaskResults(List<ForkJoinTask<List<T>>> tasks) {
        final List<T> results = new ArrayList<>();
        Iterator<ForkJoinTask<List<T>>> iterator = tasks.iterator();
        while (!tasks.isEmpty()) {
            while (iterator.hasNext()) {
                final ForkJoinTask<List<T>> task = iterator.next();
                if (task.isDone()) {
                    final List<T> jsons;
                    try {
                        jsons = task.get();
                        results.addAll(jsons);
                    } catch (InterruptedException | ExecutionException e) {
                        log.error(e.getMessage(), e);
                        Thread.currentThread().interrupt();
                    }
                    iterator.remove();
                }
            }
            iterator = tasks.iterator();
        }
        return results;
    }

    public static <T> List<T> pullTaskResult(List<ForkJoinTask<T>> tasks) {
        final List<T> results = new ArrayList<>();
        Iterator<ForkJoinTask<T>> iterator = tasks.iterator();
        while (!tasks.isEmpty()) {
            while (iterator.hasNext()) {
                final ForkJoinTask<T> task = iterator.next();
                if (task.isDone()) {
                    try {
                        results.add(task.get());
                    } catch (InterruptedException | ExecutionException e) {
                        log.error(e.getMessage(), e);
                        Thread.currentThread().interrupt();
                    }
                    iterator.remove();
                }
            }
            iterator = tasks.iterator();
        }
        return results;
    }

}
