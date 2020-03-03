package com.tsc.bitbucketbot.utils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

@Slf4j
public class LocalDateFromEpochDeserializer extends JsonDeserializer<LocalDate> {

    @Override
    public LocalDate deserialize(JsonParser jp, DeserializationContext ctxt) {
        try {
            Long time = jp.readValueAs(Long.class);
            Instant instant = Instant.ofEpochMilli(time);
            LocalDate localDate = instant.atZone(ZoneId.systemDefault()).toLocalDate();
            return localDate;
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        return LocalDate.now();
    }

}
