package com.tsc.bitbucketbot.scheduler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tsc.bitbucketbot.config.PushMessageConfig;
import com.tsc.bitbucketbot.domain.MessageSend;
import com.tsc.bitbucketbot.service.MessageSendService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Authenticator;
import okhttp3.Credentials;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@RequiredArgsConstructor
public class SchedulerPushMessageSend {

    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private final MessageSendService messageSendService;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final PushMessageConfig pushMessageConfig;
    private OkHttpClient client;

    @PostConstruct
    public void init() {
        int proxyPort = 8080;
        String proxyHost = "proxy.tsc.ts";
        final String username = "internet";
        final String password = "123454321";

        Authenticator proxyAuthenticator = (route, response) -> {
            String credential = Credentials.basic(username, password);
            return response.request().newBuilder()
                    .header("Proxy-Authorization", credential)
                    .build();
        };

        client = new OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .proxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHost, proxyPort)))
                .proxyAuthenticator(proxyAuthenticator)
                .build();
    }

    @Scheduled(fixedDelay = 15000)
    public void sendNewMessage() {
        List<MessageSend> pushMessage = messageSendService.getPushMessage();
        if (!pushMessage.isEmpty()) {
            try {
                sendMessage(objectMapper.writeValueAsString(pushMessage));
            } catch (JsonProcessingException e) {
                log.error(e.getMessage());
            }
        }
    }

    private void sendMessage(String json) {
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(pushMessageConfig.getUrl())
                .post(body)
                .build();

        try {
            client.newCall(request).execute();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

}
