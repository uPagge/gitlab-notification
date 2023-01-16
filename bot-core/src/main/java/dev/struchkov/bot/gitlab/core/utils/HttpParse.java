package dev.struchkov.bot.gitlab.core.utils;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.struchkov.haiti.utils.Inspector;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static dev.struchkov.haiti.utils.Checker.checkNotNull;
import static dev.struchkov.haiti.utils.Inspector.isNotNull;

/**
 * Утилитарный класс для работы с web.
 *
 * @author upagge 30.09.2020
 */
public class HttpParse {

    private static final Logger log = LoggerFactory.getLogger(HttpParse.class);

    public static final HttpHeader ACCEPT = HttpHeader.of("Accept", "text/html,application/xhtml+xml,application/json");

    private static final ObjectMapper objectMapper;

    private final Request.Builder requestBuilder = new Request.Builder();
    private final HttpUrl.Builder httpUrlBuilder;

    static {
        objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public HttpParse(String url) {
        Inspector.isNotNull(url);
        httpUrlBuilder = HttpUrl.parse(url).newBuilder();
    }

    public static HttpParse request(String url) {
        Inspector.isNotNull(url);
        return new HttpParse(url);
    }

    public HttpParse header(String name, String value) {
        isNotNull(name);
        if (value != null) {
            requestBuilder.header(name, value);
        }
        return this;
    }

    public HttpParse header(HttpHeader header) {
        isNotNull(header);
        requestBuilder.header(header.getName(), header.getValue());
        return this;
    }

    public HttpParse getParameter(String name, String value) {
        isNotNull(name);
        if (value != null) {
            httpUrlBuilder.addQueryParameter(name, value);
        }
        return this;
    }

    public <T> Optional<T> execute(Class<T> classOfT) {
        isNotNull(classOfT);
        final HttpUrl url = httpUrlBuilder.build();
        final Request request = requestBuilder.url(url).build();
        log.trace("Выполняется okhttp3 запрос | {}", url);
        final OkHttpClient httpClient = new OkHttpClient();
        try (final Response execute = httpClient.newCall(request).execute()) {
            log.trace("Запрос выполнен | {}", url);
            if (execute.isSuccessful() && checkNotNull(execute.body())) {
                final String string = execute.body().string();
                return Optional.ofNullable(objectMapper.readValue(string, classOfT));
            }
        } catch (IOException e) {
            log.error("Ошибка выполнения okhttp3", e);
        }
        return Optional.empty();
    }

    //TODO [16.01.2023|uPagge]: Okhttp Client создается на каждый запрос, что не рационально по потреблению ресурсов и производительности, но позволяет обойти ограничение со стороны гитлаба, при котором один и тот же клиент отбрасывался спустя 1000 запросов. Возможно стоит заменить OkHttp на что-то другое, например, RestTemplate
    public <T> List<T> executeList(Class<T> classOfT) {
        isNotNull(classOfT);
        final HttpUrl url = httpUrlBuilder.build();
        final Request request = requestBuilder.url(url).build();
        log.trace("Выполняется okhttp3 запрос | {}", url);
        final OkHttpClient httpClient = new OkHttpClient();
        try (Response execute = httpClient.newCall(request).execute()) {
            log.trace("Запрос выполнен | {}", url);
            ResponseBody body = execute.body();
            if (execute.isSuccessful() && checkNotNull(body)) {
                final String stringBody = body.string();
                final List<T> list = objectMapper.readValue(stringBody, objectMapper.getTypeFactory().constructCollectionType(List.class, classOfT));
                return (list == null || list.isEmpty()) ? Collections.emptyList() : list;
            }
        } catch (IOException e) {
            log.error("Ошибка выполнения okhttp3", e);
        }
        return Collections.emptyList();
    }

}