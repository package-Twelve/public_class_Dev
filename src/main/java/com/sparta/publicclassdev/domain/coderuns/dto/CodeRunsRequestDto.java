package com.sparta.publicclassdev.domain.coderuns.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CodeRunsRequestDto {
    private String language;
    private String code;
}
