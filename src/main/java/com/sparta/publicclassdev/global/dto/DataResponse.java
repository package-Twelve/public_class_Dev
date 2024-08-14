package com.sparta.publicclassdev.global.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class DataResponse<T> {
    private int statusCode;
    private String message;
    private T data;

    @Builder
    public DataResponse(int statusCode, String message, T data) {
        this.statusCode = statusCode;
        this.message = message;
        this.data = data;
    }
}
