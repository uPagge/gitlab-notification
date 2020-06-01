package org.sadtech.bot.bitbucketbot.utils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Slf4j
public class LocalDateTimeFromEpochDeserializer extends JsonDeserializer<LocalDateTime> {

    @Override
    public LocalDateTime deserialize(JsonParser jp, DeserializationContext ctxt) {
        try {
            Long time = jp.readValueAs(Long.class);
            Instant instant = Instant.ofEpochMilli(time);
            return instant.atZone(ZoneId.systemDefault()).toLocalDateTime();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        return null;
    }

}
