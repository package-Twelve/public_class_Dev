package com.sparta.publicclassdev.domain.codereview.dto;

import com.sparta.publicclassdev.domain.codereview.entity.CodeReviews;
import jakarta.persistence.Tuple;
import java.time.LocalDateTime;
import lombok.Getter;

@Getter
public class CodeReviewsWithUserResponseDto {

  private final Long id;
  private final String title;
  private final String name;
  private final String category;
  private final String contents;
  private final String code;
  private final LocalDateTime createdAt;
  private final LocalDateTime modifiedAt;

  public CodeReviewsWithUserResponseDto(Tuple tuple) {
    CodeReviews codeReviews = tuple.get(0, CodeReviews.class);
    String name = tuple.get(1, String.class);

    this.id = codeReviews.getId();
    this.title = codeReviews.getTitle();
    this.name = name;
    this.category = codeReviews.getCategory();
    this.contents = codeReviews.getContents();
    this.code = codeReviews.getCode();
    this.createdAt = codeReviews.getCreatedAt();
    this.modifiedAt = codeReviews.getModifiedAt();
  }
}
