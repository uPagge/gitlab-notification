package org.sadtech.bot.bitbucketbot.scheduler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Authenticator;
import okhttp3.Credentials;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.sadtech.bot.bitbucketbot.config.PushMessageConfig;
import org.sadtech.bot.bitbucketbot.domain.MessageSend;
import org.sadtech.bot.bitbucketbot.service.MessageSendService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.List;

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
        final int proxyPort = 8080;
        final String proxyHost = "proxy.tsc.ts";
        final String username = "internet";
        final String password = "123454321";

        Authenticator proxyAuthenticator = (route, response) -> {
            String credential = Credentials.basic(username, password);
            return response.request().newBuilder()
                    .header("Proxy-Authorization", credential)
                    .build();
        };

        client = new OkHttpClient.Builder()
                .proxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHost, proxyPort)))
                .proxyAuthenticator(proxyAuthenticator)
                .build();
    }

    @Scheduled(fixedDelay = 30000)
    public void sendNewMessage() {
        List<MessageSend> pushMessage = messageSendService.getPushMessage();
        if (!pushMessage.isEmpty()) {
            try {
                final String json = objectMapper.writeValueAsString(pushMessage);
//                sendMessage(json);
                System.out.println(json);
                System.out.println("\n\n");
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
        try (final Response response = client.newCall(request).execute()) {
            if (response.code() != 200) {
                log.error("Ошибка отправки сообщения: " + response);
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

}
