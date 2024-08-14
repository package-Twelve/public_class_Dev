package com.sparta.publicclassdev.domain.users.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReissueTokenRequestDto {
    private String refreshToken;

    public ReissueTokenRequestDto(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
