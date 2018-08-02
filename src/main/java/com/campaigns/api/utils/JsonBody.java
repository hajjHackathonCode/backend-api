package com.campaigns.api.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.ResponseEntity;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JsonBody<T> implements Serializable
{
    private T response;

    public static <T> ResponseEntity<JsonBody<T>> ok(T body)
    {
        return ResponseEntity.ok(new JsonBody<>(body));
    }
}
