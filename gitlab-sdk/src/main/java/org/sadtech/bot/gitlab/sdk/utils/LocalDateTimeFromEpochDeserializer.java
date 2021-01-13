package org.sadtech.bot.gitlab.sdk.utils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class LocalDateTimeFromEpochDeserializer extends JsonDeserializer<LocalDateTime> {

    @Override
    public LocalDateTime deserialize(JsonParser jp, DeserializationContext ctxt) {
        try {
            Long time = jp.readValueAs(Long.class);
            Instant instant = Instant.ofEpochMilli(time);
            return instant.atZone(ZoneId.systemDefault()).toLocalDateTime();
        } catch (IOException e) {

        }
        return null;
    }

}
