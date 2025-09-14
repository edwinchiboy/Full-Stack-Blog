package com.blog.cutom_blog.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.io.IOException;

import java.util.Set;
import java.util.stream.Collectors;


@Slf4j
public class Utils {

    private static final ObjectReader objectReader;
    private static final ObjectWriter objectWriter;
    private static final ObjectMapper objectMapper;
    static {
        objectMapper = new ObjectMapper();
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        objectMapper.registerModule(new JavaTimeModule());
        objectReader = objectMapper.reader();
        objectWriter = objectMapper.writer();

    }

    public static  <T> T parseJson(final String jsonData, final Class<T> clazz) throws IOException {
        return objectReader.forType(clazz).readValue(jsonData);
    }

    public static  <T> T parseJson(final JsonNode jsonData, final Class<T> clazz) throws IOException {
        return objectReader.forType(clazz).readValue(jsonData);
    }

    public static  <T> T parseJson(final String jsonData, final TypeReference<T> clazz) throws IOException {
        return objectReader.forType(clazz).readValue(jsonData);
    }

    public static  <T> T convert(final Object source, final TypeReference<T> clazz) {
        return objectMapper.convertValue(source, clazz);
    }

    public static  <T> T convert(final Object source, final Class<T> clazz) {
        return objectMapper.convertValue(source, clazz);
    }

    public static  <T> String writeJson(final T data) throws JsonProcessingException {
        return objectWriter.forType(data.getClass()).writeValueAsString(data);
    }

    public static Pageable defaultPageRequest(final int page, final int size){
        return PageRequest.of(page, size == 0 ? 20 : size, Sort.by(Sort.Direction.DESC, "createdAt"));
    }

    public static Pageable defaultPageRequestWithoutSort(final int page, final int size){
        return PageRequest.of(page, size == 0 ? 20 : size);
    }

    public static <T> T or(T main, T alternative){
        return main != null ? main : alternative;
    }



    public static int getMatchPercentage(@NonNull final Set<String> a, @NonNull final Set<String> b){
        var referenceTotalCount = Math.min(a.size(), b.size());
        if(referenceTotalCount == 0) return 100;
        var formattedB = b.stream().map(String::toLowerCase).collect(Collectors.toSet());
        var matchesCount = 0;
        for(var val : a){
            if(formattedB.contains(val.toLowerCase())) ++matchesCount;
        }

        log.info("a: {}, b: {}, matchesCount: {}, referenceTotalCount: {}", a, b, matchesCount, referenceTotalCount);
        return 100 * matchesCount/referenceTotalCount;
    }

    public static String capitalizeFirstChar(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }

        return String.format("%s%s", Character.toUpperCase(input.charAt(0)), input.substring(1).toLowerCase());
    }

}
