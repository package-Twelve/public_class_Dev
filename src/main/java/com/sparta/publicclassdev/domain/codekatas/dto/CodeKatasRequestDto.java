package com.sparta.publicclassdev.domain.codekatas.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CodeKatasRequestDto {
    
    private String title;
    private String contents;
    
    public CodeKatasRequestDto(String title, String contents) {
        this.title = title;
        this.contents = contents;
    }
}
