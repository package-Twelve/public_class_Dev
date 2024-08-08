package com.sparta.publicclassdev.domain.codereviewcomment.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class CodeReviewCommentsRequestDto {
  
  @NotBlank(message = "내용을 입력해주세요.")
  private String contents;
}
