package com.sparta.publicclassdev.domain.users.dto;

import com.sparta.publicclassdev.domain.users.entity.Users;
import lombok.Getter;

@Getter
public class UpdateProfileResponseDto {
    private String name;
    private String intro;

    public UpdateProfileResponseDto(Users user) {
        this.name = user.getName();
        this.intro = user.getIntro();
    }
}
