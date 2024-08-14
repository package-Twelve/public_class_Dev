package com.sparta.publicclassdev.domain.winners.dto;

import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class WinnersRequestDto {
    
    private String code;
    private String language;
    private Long responseTime;
    private String result;
    private String teamName;
    private LocalDate date;
    private Long codeKatasId;
    private Long codeRunsId;
    private Long teamsId;
    
    public WinnersRequestDto(String code, String language, Long responseTime, String result, String teamName,
        LocalDate date, Long codeKatasId, Long codeRunsId, Long teamsId) {
        this.code = code;
        this.language = language;
        this.responseTime = responseTime;
        this.result = result;
        this.teamName = teamName;
        this.date = date;
        this.codeKatasId = codeKatasId;
        this.codeRunsId = codeRunsId;
        this.teamsId = teamsId;
    }
}
