package com.sparta.publicclassdev.domain.communities.dto;

import com.sparta.publicclassdev.domain.communities.entity.Communities.Category;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;

@Getter
public class CommunitiesRequestDto {
    @NotBlank(message = "제목을 작성해주세요")
    String title;
    @NotBlank(message = "내용을 작성해주세요")
    String content;
    @NotNull(message = "카테고리를 지정해주세요")
    Category category;

    @Builder
    public CommunitiesRequestDto(String title, String content, Category category) {
        this.title = title;
        this.content = content;
        this.category = category;
    }
}
