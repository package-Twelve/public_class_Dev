package com.sparta.publicclassdev.domain.winners.dto;

import com.sparta.publicclassdev.domain.winners.entity.Winners;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class WinnersResponseDto {
    
    private Long id;
    private String code;
    private String language;
    private Long responseTime;
    private String result;
    private String teamName;
    private LocalDate date;
    
    @Builder
    public WinnersResponseDto(Long id, String code, String language, Long responseTime,
        LocalDate date, String result, String teamName) {
        this.id = id;
        this.code = code;
        this.language = language;
        this.responseTime = responseTime;
        this.result = result;
        this.teamName = teamName;
        this.date = date;
    }
    
}
