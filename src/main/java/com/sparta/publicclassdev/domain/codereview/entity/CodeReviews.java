package com.sparta.publicclassdev.domain.codereview.entity;

import com.sparta.publicclassdev.domain.codereview.dto.CodeReviewsRequestDto;
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
@Table(name = "codereviews")
public class CodeReviews extends Timestamped {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String title;

  @Column(nullable = false)
  private String category;

  @Column(columnDefinition = "TEXT", nullable = false)
  private String contents;

  @Column
  private String code;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private Status status;

  @ManyToOne
  @JoinColumn(name = "user_id")
  private Users user;

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

  public void updateCodeReview(CodeReviewsRequestDto codeReviewsRequestDto) {
    this.title = codeReviewsRequestDto.getTitle();
    this.contents = codeReviewsRequestDto.getContents();
  }

  public void updateCategory(String category) {
    this.category = category;
  }

  public void updateCode(String code) {
    this.code = code;
  }
}
