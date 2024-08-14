package com.sparta.publicclassdev.global.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MessageResponse {
    private int statusCode;
    private String message;

    @Builder
    public MessageResponse(int statusCode, String message) {
        this.statusCode = statusCode;
        this.message = message;
    }
}