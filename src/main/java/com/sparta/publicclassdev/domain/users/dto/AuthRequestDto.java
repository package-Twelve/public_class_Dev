package com.sparta.publicclassdev.domain.users.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AuthRequestDto {
    @NotBlank
    private String email;
    @NotBlank
    private String password;

    public AuthRequestDto(String email, String password) {
        this.email = email;
        this.password = password;
    }
}
