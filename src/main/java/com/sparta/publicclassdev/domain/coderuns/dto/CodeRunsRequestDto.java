package com.sparta.publicclassdev.domain.coderuns.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CodeRunsRequestDto {
    @NotBlank
    private String language;
    @NotBlank
    private String code;
    
    @Builder
    public CodeRunsRequestDto(String language, String code) {
        this.language = language;
        this.code = code;
    }
}
