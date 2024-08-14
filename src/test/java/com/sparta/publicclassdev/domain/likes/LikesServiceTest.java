package com.sparta.publicclassdev.domain.likes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.sparta.publicclassdev.domain.codereview.dto.CodeReviewsRequestDto;
import com.sparta.publicclassdev.domain.codereview.entity.CodeReviews;
import com.sparta.publicclassdev.domain.codereview.entity.CodeReviews.Status;
import com.sparta.publicclassdev.domain.codereview.repository.CodeReviewsRepository;
import com.sparta.publicclassdev.domain.codereviewcomment.dto.CodeReviewCommentsRequestDto;
import com.sparta.publicclassdev.domain.codereviewcomment.entity.CodeReviewComments;
import com.sparta.publicclassdev.domain.codereviewcomment.repository.CodeReviewCommentsRepository;
import com.sparta.publicclassdev.domain.likes.entity.Likes;
import com.sparta.publicclassdev.domain.likes.repository.LikesRepository;
import com.sparta.publicclassdev.domain.likes.service.LikesService;
import com.sparta.publicclassdev.domain.users.entity.RoleEnum;
import com.sparta.publicclassdev.domain.users.entity.Users;
import com.sparta.publicclassdev.domain.users.repository.UsersRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

@SpringBootTest
@ActiveProfiles("test")
public class LikesServiceTest {


  private String testUserName = "testuser";
  private String testUserEmail = "test@email.com";
  private String testUserPassword = "Test123!";
  private RoleEnum testUserRole = RoleEnum.USER;
  private String testCodeReviewTitle = "Title";
  private String testCodeReviewCategory = "#category ";
  private String testCodeReviewContents = "Contents";
  private String testCommentContents = "Comment";
  private String testCode = "testcode.txt";


  @Autowired
  private CodeReviewsRepository codeReviewsRepository;

  @Autowired
  private CodeReviewCommentsRepository codeReviewCommentsRepository;

  @Autowired
  private UsersRepository usersRepository;

  @Autowired
  private LikesRepository likesRepository;

  @Autowired
  private LikesService likesService;

  private Users createTestUser() {
    return Users.builder()
        .name(testUserName)
        .email(testUserEmail)
        .password(testUserPassword)
        .role(testUserRole)
        .build();
  }

  private CodeReviews createTestCodeReviews(Users user) {
    return CodeReviews.builder()
        .title(testCodeReviewTitle)
        .category(testCodeReviewCategory)
        .contents(testCodeReviewContents)
        .code(testCode)
        .status(Status.ACTIVE)
        .user(user)
        .build();
  }

  private CodeReviewComments createTestCodeReviewComments(CodeReviews codeReview, Users user) {
    return CodeReviewComments.builder()
        .contents(testCommentContents)
        .status(CodeReviewComments.Status.ACTIVE)
        .user(user)
        .codeReviews(codeReview)
        .build();
  }

  private CodeReviewsRequestDto createTestCodeReviewsRequestDto() {
    CodeReviewsRequestDto requestDto = new CodeReviewsRequestDto();

    ReflectionTestUtils.setField(requestDto, "title", testCodeReviewTitle);
    ReflectionTestUtils.setField(requestDto, "contents", testCodeReviewContents);
    ReflectionTestUtils.setField(requestDto, "category", testCodeReviewCategory);
    ReflectionTestUtils.setField(requestDto, "code", "Code");

    return requestDto;
  }

  private CodeReviewCommentsRequestDto createTestCodeReviewCommentsRequestDto() {
    CodeReviewCommentsRequestDto requestDto = new CodeReviewCommentsRequestDto();

    ReflectionTestUtils.setField(requestDto, "contents", testCommentContents);
    return requestDto;
  }

  @Nested
  class SetLikeTest {

    @Test
    @Transactional
    void testSetLike_NoLikeExists() {
      // given
      Users user = createTestUser();
      usersRepository.save(user);

      CodeReviews codeReview = createTestCodeReviews(user);
      codeReviewsRepository.save(codeReview);

      CodeReviewComments comment = createTestCodeReviewComments(codeReview, user);
      codeReviewCommentsRepository.save(comment);

      // when
      String resultMessage = likesService.setLike(codeReview.getId(), comment.getId(), user);

      // then
      assertEquals("코드 리뷰 댓글 좋아요 추가 완료", resultMessage);

      Likes like = likesRepository.findByUserIdAndCodeReviewCommentId(user.getId(),
          comment.getId());
      assertNotNull(like);
      assertEquals(Likes.Status.LIKED, like.getStatus());
    }

    @Test
    @Transactional
    void testSetLikeWhen_LikeExists() {
      // given
      Users user = createTestUser();
      usersRepository.save(user);

      CodeReviews codeReview = createTestCodeReviews(user);
      codeReviewsRepository.save(codeReview);

      CodeReviewComments comment = createTestCodeReviewComments(codeReview, user);
      codeReviewCommentsRepository.save(comment);

      Likes like = Likes.builder()
          .status(Likes.Status.LIKED)
          .user(user)
          .codeReviewComment(comment)
          .build();

      likesRepository.save(like);

      // when
      String result = likesService.setLike(codeReview.getId(), comment.getId(), user);

      // then
      assertEquals("코드 리뷰 댓글 좋아요 삭제 완료", result);

      Likes updatedLike = likesRepository.findByUserIdAndCodeReviewCommentId(user.getId(),
          comment.getId());
      assertNotNull(updatedLike);
      assertEquals(Likes.Status.UNLIKED, updatedLike.getStatus());
    }

    @Test
    @Transactional
    void testSetLike_LikeDeleted() {
      // given
      Users user = createTestUser();
      usersRepository.save(user);

      CodeReviews codeReview = createTestCodeReviews(user);
      codeReviewsRepository.save(codeReview);

      CodeReviewComments comment = createTestCodeReviewComments(codeReview, user);
      codeReviewCommentsRepository.save(comment);

      Likes like = Likes.builder()
          .status(Likes.Status.UNLIKED)
          .user(user)
          .codeReviewComment(comment)
          .build();

      likesRepository.save(like);

      // when
      String result = likesService.setLike(codeReview.getId(), comment.getId(), user);

      // then
      assertEquals("코드 리뷰 댓글 좋아요 추가 완료", result);

      Likes updatedLike = likesRepository.findByUserIdAndCodeReviewCommentId(user.getId(),
          comment.getId());
      assertNotNull(updatedLike);
      assertEquals(Likes.Status.LIKED, updatedLike.getStatus());
    }
  }
}
