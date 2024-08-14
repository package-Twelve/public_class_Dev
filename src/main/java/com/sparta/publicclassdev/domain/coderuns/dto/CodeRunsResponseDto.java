package com.sparta.publicclassdev.domain.coderuns.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CodeRunsResponseDto {
    
    private Long id;
    private Long codeKatasId;
    private Long teamsId;
    private Long usersId;
    private Long responseTime;
    private String result;
    private String code;
    private String language;
    
    @Builder
    public CodeRunsResponseDto(Long id, Long codeKatasId, Long teamsId, Long usersId, Long responseTime, String result, String code, String language) {
        this.id = id;
        this.codeKatasId = codeKatasId;
        this.teamsId = teamsId;
        this.usersId = usersId;
        this.responseTime = responseTime;
        this.result = result;
        this.code = code;
        this.language = language;
    }
}
