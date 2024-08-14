package com.sparta.publicclassdev.domain.codereviewcomment.entity;

import com.sparta.publicclassdev.domain.codereview.entity.CodeReviews;
import com.sparta.publicclassdev.domain.codereviewcomment.dto.CodeReviewCommentsRequestDto;
import com.sparta.publicclassdev.domain.users.entity.Users;
import com.sparta.publicclassdev.global.entity.Timestamped;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "codereviewcomments")
public class CodeReviewComments extends Timestamped {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String contents;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private Status status;

  @ManyToOne
  @JoinColumn(name = "user_id")
  private Users user;

  @ManyToOne
  @JoinColumn(name = "code_review_id")
  private CodeReviews codeReviews;

  public enum Status {
    ACTIVE,
    DELETED;
  }

  public void delete() {
    this.status = Status.DELETED;
  }

  public void active() {
    this.status = Status.ACTIVE;
  }

  public void updateCodeReviewComment(CodeReviewCommentsRequestDto codeReviewCommentsRequestDto) {
    this.contents = codeReviewCommentsRequestDto.getContents();
  }
}
