package com.sparta.publicclassdev.domain.likes.entity;

import com.sparta.publicclassdev.domain.codereviewcomment.entity.CodeReviewComments;
import com.sparta.publicclassdev.domain.users.entity.Users;
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
@Table(name = "likes")
public class Likes {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private Status status;

  @ManyToOne
  @JoinColumn(name = "user_id")
  private Users user;

  @ManyToOne
  @JoinColumn(name = "code_review_comment_id")
  private CodeReviewComments codeReviewComment;

  public enum Status {
    LIKED,
    UNLIKED;
  }

  public void addLike() {
    this.status = Status.LIKED;
  }

  public void removeLike() {
    this.status = Status.UNLIKED;
  }
}
