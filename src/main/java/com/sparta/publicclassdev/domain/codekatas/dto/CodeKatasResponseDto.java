package com.sparta.publicclassdev.domain.codekatas.dto;

import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CodeKatasResponseDto {

    private Long id;
    private String title;
    private String contents;
    private LocalDate markDate;

    public CodeKatasResponseDto(Long id, String title, String contents, LocalDate markDate) {
        this.id = id;
        this.title = title;
        this.contents = contents;
        this.markDate = markDate;
    }
}
