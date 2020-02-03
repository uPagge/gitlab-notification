package com.tsc.bitbucketbot.service;

import com.google.gson.Gson;
import lombok.NonNull;
import org.springframework.stereotype.Service;

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
@Service
public class Utils {

    private static Gson gson = new Gson();

    public static <T> Optional<T> urlToJson(@NonNull String urlValue, String token, Class<T> classOfT) {
        StringBuilder sb = null;
        URL url;
        URLConnection urlCon;
        try {
            url = new URL(urlValue);
            urlCon = url.openConnection();
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

        }
        if (sb != null) {
            return Optional.of(gson.fromJson(sb.toString(), classOfT));
        }
        return Optional.empty();
    }

}
