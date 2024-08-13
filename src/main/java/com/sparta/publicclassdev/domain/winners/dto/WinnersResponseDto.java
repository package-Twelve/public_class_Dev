package com.sparta.publicclassdev.domain.winners.dto;

import java.time.LocalDate;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class WinnersResponseDto {
    
    private Long id;
    private String code;
    private String language;
    private Long responseTime;
    private String result;
    private String teamName;
    private LocalDate date;
    private String codeKataTitle;
    private String codeKataContents;
    
    @Builder
    public WinnersResponseDto(Long id, String code, String language, Long responseTime, String result, String teamName,
        LocalDate date, String codeKataTitle, String codeKataContents) {
        this.id = id;
        this.code = code;
        this.language = language;
        this.responseTime = responseTime;
        this.result = result;
        this.teamName = teamName;
        this.date = date;
        this.codeKataTitle = codeKataTitle;
        this.codeKataContents = codeKataContents;
    }
}
