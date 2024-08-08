package com.sparta.publicclassdev.domain.coderuns.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CodeRunsRequestDto {
    @NotBlank
    private String language;
    @NotBlank
    private String code;
}
