package com.sparta.publicclassdev.domain.winners.dto;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
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
}
