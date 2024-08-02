package com.sparta.publicclassdev.domain.codekatas.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CodeKatasDto {

    private Long id;
    private String title;
    private String contents;
    private LocalDate markDate;

    public CodeKatasDto(Long id, String title, String contents, LocalDate markDate) {
        this.id = id;
        this.title = title;
        this.contents = contents;
        this.markDate = markDate;
    }
}
