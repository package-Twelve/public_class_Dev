package com.sparta.publicclassdev.domain.users.dto;

import jakarta.validation.constraints.Pattern;
import lombok.Getter;

@Getter
public class PasswordRequestDto {
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[`~!@#$%^])[A-Za-z\\d`~!@#$%^]{8,15}$",
        message = "비밀번호는 대소문자 포함 영문 + 숫자 + 특수문자를 최소 1글자씩 포함해서 8자 이상 15자여야 합니다.")
    private String password;
}
