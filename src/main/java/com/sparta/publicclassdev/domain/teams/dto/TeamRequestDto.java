package com.sparta.publicclassdev.domain.teams.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TeamRequestDto {

    private String email;
    
    public TeamRequestDto(String email) {
        this.email = email;
    }
}
