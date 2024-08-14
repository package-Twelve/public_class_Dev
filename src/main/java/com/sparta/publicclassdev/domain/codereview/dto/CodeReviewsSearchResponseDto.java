package com.sparta.publicclassdev.domain.codereview.dto;

import java.util.List;
import lombok.Getter;

@Getter
public class CodeReviewsSearchResponseDto {

  private final String searchCategory;
  private final Integer currentPage;
  private final Integer totalPages;
  private final Long totalItems;
  private final List<CodeReviewsWithUserResponseDto> items;

  public CodeReviewsSearchResponseDto(
      String category,
      Integer currentPage,
      Integer totalPages,
      Long totalItems,
      List<CodeReviewsWithUserResponseDto> items) {

    this.searchCategory = category;
    this.currentPage = currentPage;
    this.totalPages = totalPages;
    this.totalItems = totalItems;
    this.items = items;
  }
}
