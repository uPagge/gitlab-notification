package com.tsc.bitbucketbot.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Optional;
import java.util.zip.GZIPInputStream;

/**
 * TODO: Добавить описание класса.
 *
 * @author upagge [30.01.2020]
 */
@Slf4j
public class Utils {

    private static ObjectMapper objectMapper;

    static {
        objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    private Utils() {
        throw new IllegalStateException("Утилитарный класс");
    }

    @NonNull
    public static <T> Optional<T> urlToJson(String urlValue, String token, Class<T> classOfT) {
        StringBuilder sb = null;
        URLConnection urlCon;
        try {
            urlCon = new URL(urlValue).openConnection();
            if (token != null) {
                urlCon.setRequestProperty("Authorization", "Bearer " + token);
            }
            BufferedReader in;
            if (urlCon.getHeaderField("Content-Encoding") != null
                    && urlCon.getHeaderField("Content-Encoding").equals("gzip")) {
                in = new BufferedReader(new InputStreamReader(new GZIPInputStream(urlCon.getInputStream())));
            } else {
                in = new BufferedReader(new InputStreamReader(urlCon.getInputStream()));
            }
            String inputLine;
            sb = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                sb.append(inputLine);
            }
            in.close();
        } catch (IOException e) {
            log.trace(e.getMessage());
        }
        if (sb != null) {
            try {
                return Optional.of(objectMapper.readValue(sb.toString(), classOfT));
            } catch (JsonProcessingException e) {
                log.error(e.getMessage());
            }
        }
        return Optional.empty();
    }

}
