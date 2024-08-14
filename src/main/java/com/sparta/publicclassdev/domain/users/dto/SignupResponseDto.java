package com.sparta.publicclassdev.domain.users.dto;

import com.sparta.publicclassdev.domain.users.entity.RoleEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Builder
@Getter
public class SignupResponseDto {
    private String name;
    private String email;
    private RoleEnum role;
}
