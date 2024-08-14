package com.sparta.publicclassdev.domain.codereview.dto;

import java.util.List;
import lombok.Getter;

@Getter
public class CodeReviewsListResponseDto {

  private final Integer currentPage;
  private final Integer totalPages;
  private final Long totalItems;
  private final List<CodeReviewsWithUserResponseDto> items;

  public CodeReviewsListResponseDto(
      Integer currentPage,
      Integer totalPages,
      Long totalItems,
      List<CodeReviewsWithUserResponseDto> items) {

    this.currentPage = currentPage;
    this.totalPages = totalPages;
    this.totalItems = totalItems;
    this.items = items;
  }
}
