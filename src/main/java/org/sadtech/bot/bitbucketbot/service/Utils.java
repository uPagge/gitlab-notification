package org.sadtech.bot.bitbucketbot.service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * @author upagge [30.01.2020]
 */
@Slf4j
public class Utils {

    private static ObjectMapper objectMapper;

    static {
        objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    private static final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .build();

    private Utils() {
        throw new IllegalStateException("Утилитарный класс");
    }

    @NonNull
    public static <T> Optional<T> urlToJson(String urlValue, String token, Class<T> classOfT) {
        Request request = new Request.Builder()
                .url(urlValue)
                .header("Authorization", "Bearer " + token)
                .build();
        try (final Response execute = client.newCall(request).execute()) {
            if (execute.isSuccessful() && execute.body() != null) {
                return Optional.ofNullable(objectMapper.readValue(execute.body().string(), classOfT));
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        return Optional.empty();
    }

}
