package com.sparta.publicclassdev.domain.likes.service;

import com.sparta.publicclassdev.domain.codereview.entity.CodeReviews;
import com.sparta.publicclassdev.domain.codereview.entity.CodeReviews.Status;
import com.sparta.publicclassdev.domain.codereview.repository.CodeReviewsRepository;
import com.sparta.publicclassdev.domain.codereviewcomment.entity.CodeReviewComments;
import com.sparta.publicclassdev.domain.codereviewcomment.repository.CodeReviewCommentsRepository;
import com.sparta.publicclassdev.domain.likes.entity.Likes;
import com.sparta.publicclassdev.domain.likes.repository.LikesRepository;
import com.sparta.publicclassdev.domain.users.entity.RoleEnum;
import com.sparta.publicclassdev.domain.users.entity.Users;
import com.sparta.publicclassdev.domain.users.repository.UsersRepository;
import com.sparta.publicclassdev.global.exception.CustomException;
import com.sparta.publicclassdev.global.exception.ErrorCode;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LikesService {

  private final LikesRepository likesRepository;
  private final CodeReviewCommentsRepository codeReviewCommentsRepository;
  private final CodeReviewsRepository codeReviewsRepository;
  private final UsersRepository usersRepository;

  @Transactional
  public String setLike(Long codeReviewsId, Long codeReviewCommentsId, Users user) {

    Users foundUser = validateUser(user);

    validateCodeReviewId(codeReviewsId);

    CodeReviewComments foundCodeReviewComments = validateCodeReviewCommentId(codeReviewCommentsId);

    Likes foundLike = likesRepository.findByUserIdAndCodeReviewCommentId(foundUser.getId(),
        codeReviewCommentsId);

    if (foundLike == null) {
      Likes like = Likes.builder()
          .status(Likes.Status.LIKED)
          .user(foundUser)
          .codeReviewComment(foundCodeReviewComments)
          .build();

      likesRepository.save(like);

      return "코드 리뷰 댓글 좋아요 추가 완료";
    } else if (foundLike.getStatus().equals(Likes.Status.LIKED)) {
      foundLike.removeLike();
      return "코드 리뷰 댓글 좋아요 삭제 완료";
    } else {
      foundLike.addLike();
      return "코드 리뷰 댓글 좋아요 추가 완료";
    }
  }

  public Users validateUser(Users user) {
    Users foundUser = usersRepository.findByEmail(user.getEmail()).orElseThrow(
        () -> new CustomException(ErrorCode.USER_NOT_FOUND)
    );

    if (foundUser.getRole().equals(RoleEnum.WITHDRAW)) {
      throw new CustomException(ErrorCode.USER_NOT_FOUND);
    }

    return foundUser;
  }

  public CodeReviews validateCodeReviewId(Long codeReviewsId) {
    CodeReviews foundCodeReviews = codeReviewsRepository.findById(codeReviewsId).orElseThrow(
        () -> new CustomException(ErrorCode.NOT_FOUND_CODEREVIEW_POST)
    );

    if (foundCodeReviews.getStatus().equals(Status.DELETED)) {
      throw new CustomException(ErrorCode.NOT_FOUND_CODEREVIEW_POST);
    }

    return foundCodeReviews;
  }

  public CodeReviewComments validateCodeReviewCommentId(Long codeReviewCommentId) {
    CodeReviewComments foundCodeReviewComments = codeReviewCommentsRepository.findById(
        codeReviewCommentId).orElseThrow(
        () -> new CustomException(ErrorCode.NOT_FOUND_CODEREVIEW_COMMENT)
    );

    if (foundCodeReviewComments.getStatus().equals(CodeReviewComments.Status.DELETED)) {
      throw new CustomException(ErrorCode.NOT_FOUND_CODEREVIEW_COMMENT);
    }

    return foundCodeReviewComments;
  }
}
