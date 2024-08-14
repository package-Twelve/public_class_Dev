package com.sparta.publicclassdev.domain.codereviewcomment.dto;

import com.sparta.publicclassdev.domain.codereviewcomment.entity.CodeReviewComments;
import jakarta.persistence.Tuple;
import java.time.LocalDateTime;
import lombok.Getter;

@Getter
public class CodeReviewCommentsWithLikesResponseDto {

  private final Long id;
  private final String contents;
  private final String name;
  private final Long likes;
  private final LocalDateTime createdAt;
  private final LocalDateTime modifiedAt;

  public CodeReviewCommentsWithLikesResponseDto(Tuple tuple) {
    CodeReviewComments codeReviewComments = tuple.get(0, CodeReviewComments.class);
    String name = tuple.get(1, String.class);
    Long likes = tuple.get(2, Long.class);

    this.id = codeReviewComments.getId();
    this.contents = codeReviewComments.getContents();
    this.name = name;
    this.likes = likes;
    this.createdAt = codeReviewComments.getCreatedAt();
    this.modifiedAt = codeReviewComments.getModifiedAt();
  }
}
