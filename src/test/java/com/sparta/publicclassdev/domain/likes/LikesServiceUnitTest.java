package com.sparta.publicclassdev.domain.likes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

import com.sparta.publicclassdev.domain.codereview.entity.CodeReviews;
import com.sparta.publicclassdev.domain.codereview.repository.CodeReviewsRepository;
import com.sparta.publicclassdev.domain.codereviewcomment.entity.CodeReviewComments;
import com.sparta.publicclassdev.domain.codereviewcomment.repository.CodeReviewCommentsRepository;
import com.sparta.publicclassdev.domain.likes.service.LikesService;
import com.sparta.publicclassdev.domain.users.entity.RoleEnum;
import com.sparta.publicclassdev.domain.users.entity.Users;
import com.sparta.publicclassdev.domain.users.repository.UsersRepository;
import com.sparta.publicclassdev.global.exception.CustomException;
import com.sparta.publicclassdev.global.exception.ErrorCode;
import java.util.Optional;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
public class LikesServiceUnitTest {

  private String testUserName = "testuser";
  private String testUserEmail = "test@email.com";
  private String testUserPassword = "Test123!";
  private RoleEnum testUserRole = RoleEnum.USER;
  private String testCodeReviewTitle = "Title";
  private String testCodeReviewCategory = "#category ";
  private String testCodeReviewContents = "Contents";
  private Long testUserId = 1L;
  private Long testCodeReviewId = 1L;
  private Long testCommentId = 1L;


  @Mock
  private CodeReviewsRepository codeReviewsRepository;

  @Mock
  private CodeReviewCommentsRepository codeReviewCommentsRepository;

  @Mock
  private UsersRepository usersRepository;

  @InjectMocks
  private LikesService likesService;

  private Users createTestUser() {
    Users user = Users.builder()
        .name(testUserName)
        .email(testUserEmail)
        .password(testUserPassword)
        .role(testUserRole)
        .build();

    ReflectionTestUtils.setField(user, "id", testUserId);

    return user;
  }

  private CodeReviews createTestCodeReviews(Users user) {
    String code = createTestCode(testCodeReviewId);

    return CodeReviews.builder()
        .id(testCodeReviewId)
        .title(testCodeReviewTitle)
        .category(testCodeReviewCategory)
        .contents(testCodeReviewContents)
        .code(code)
        .status(CodeReviews.Status.ACTIVE)
        .user(user)
        .build();
  }

  private CodeReviewComments createTestCodeReviewComments(CodeReviews codeReview, Users user) {
    return CodeReviewComments.builder()
        .id(testCommentId)
        .contents("Comment")
        .status(CodeReviewComments.Status.ACTIVE)
        .user(user)
        .codeReviews(codeReview)
        .build();
  }

  private String createTestCode(Long codeReviewId) {
    return "codereviews-code/code-" + codeReviewId + ".txt";
  }

  @Nested
  class ValidateUserTest {

    @Test
    void testValidateUser() {
      // given
      Users user = createTestUser();

      given(usersRepository.findByEmail(any(String.class))).willReturn(Optional.of(user));

      // when
      Users validatedUser = likesService.validateUser(user);

      // then
      assertNotNull(validatedUser);
      assertEquals(testUserEmail, validatedUser.getEmail());
    }

    @Test
    void testValidateUser_UserNotFound() {
      // given
      testUserEmail = "wrong@example.com";
      Users user = createTestUser();

      given(usersRepository.findByEmail(any(String.class))).willReturn(Optional.empty());

      // when & then
      CustomException exception = assertThrows(CustomException.class, () -> {
        likesService.validateUser(user);
      });
      assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());
    }

  }

  @Nested
  class ValidateCodeReviewIdTest {

    @Test
    void testValidateCodeReviewId() {
      // given
      Users user = createTestUser();
      CodeReviews codeReview = createTestCodeReviews(user);

      given(codeReviewsRepository.findById(anyLong())).willReturn(Optional.of(codeReview));

      // when
      CodeReviews validatedCodeReview = likesService.validateCodeReviewId(testCodeReviewId);

      // then
      assertNotNull(validatedCodeReview);
      assertEquals(testCodeReviewId, validatedCodeReview.getId());
    }

    @Test
    void testValidateCodeReviewId_CodeReviewNotFound() {
      // given
      given(codeReviewsRepository.findById(anyLong())).willReturn(Optional.empty());

      // when & then
      CustomException exception = assertThrows(CustomException.class, () -> {
        likesService.validateCodeReviewId(testCodeReviewId);
      });
      assertEquals(ErrorCode.NOT_FOUND_CODEREVIEW_POST, exception.getErrorCode());
    }

  }

  @Nested
  class ValidateCodeReviewCommentIdTest {

    @Test
    void testValidateCodeReviewCommentId() {
      // given
      Users user = createTestUser();
      CodeReviews codeReview = createTestCodeReviews(user);
      CodeReviewComments codeReviewComment = createTestCodeReviewComments(codeReview, user);

      given(codeReviewCommentsRepository.findById(anyLong())).willReturn(
          Optional.of(codeReviewComment));

      // when
      CodeReviewComments validatedComment = likesService.validateCodeReviewCommentId(testCommentId);

      // then
      assertNotNull(validatedComment);
      assertEquals(testCommentId, validatedComment.getId());
    }

    @Test
    void testValidateCodeReviewCommentId_CodeReviewCommentNotFound() {
      // given
      given(codeReviewCommentsRepository.findById(anyLong())).willReturn(Optional.empty());

      // when & then
      CustomException exception = assertThrows(CustomException.class, () -> {
        likesService.validateCodeReviewCommentId(testCommentId);
      });
      assertEquals(ErrorCode.NOT_FOUND_CODEREVIEW_COMMENT, exception.getErrorCode());
    }
  }
}
