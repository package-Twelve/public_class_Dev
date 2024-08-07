package com.sparta.publicclassdev.domain.communities.dto;

import com.sparta.publicclassdev.domain.communities.entity.Communities.Category;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class CommunitiesRequestDto {
    @NotBlank(message = "제목을 작성해주세요")
    String title;
    @Size(min = 5, max = 100, message = "5글자 이상 100글자 이하로 작성해주세요")
    @NotBlank(message = "내용을 작성해주세요")
    String content;
    @NotNull(message = "카테고리를 지정해주세요")
    Category category;
}
