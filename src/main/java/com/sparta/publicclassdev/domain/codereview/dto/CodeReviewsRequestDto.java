package com.sparta.publicclassdev.domain.codereview.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class CodeReviewsRequestDto {
  @NotBlank(message = "제목을 입력해주세요.")
  private String title;

  @NotBlank(message = "카테고리는 한 개 이상 입력해주세요.")
  private String category;

  @NotBlank(message = "내용을 입력해주세요.")
  private String contents;

  private String code;
}
