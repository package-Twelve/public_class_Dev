package com.sparta.publicclassdev.domain.codereviewcomment.dto;

import com.sparta.publicclassdev.domain.codereviewcomment.entity.CodeReviewComments;
import com.sparta.publicclassdev.domain.users.entity.Users;
import java.time.LocalDateTime;
import lombok.Getter;

@Getter
public class CodeReviewCommentsResponseDto {

  private final Long id;
  private final String contents;
  private final String name;
  private final LocalDateTime createdAt;
  private final LocalDateTime modifiedAt;

  public CodeReviewCommentsResponseDto(CodeReviewComments codeReviewComments, Users users) {
    this.id = codeReviewComments.getId();
    this.contents = codeReviewComments.getContents();
    this.name = users.getName();
    this.createdAt = codeReviewComments.getCreatedAt();
    this.modifiedAt = codeReviewComments.getModifiedAt();
  }
}
