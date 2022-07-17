package com.polovyi.ivan.tutorials.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.SneakyThrows;

public class JsonConverter {

    private static final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @SneakyThrows
    public static <T> T stringJsonToObject(String json, Class<T> clazz) {
        return mapper.readValue(json, clazz);
    }

}
