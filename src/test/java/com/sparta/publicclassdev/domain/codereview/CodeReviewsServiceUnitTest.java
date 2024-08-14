package com.sparta.publicclassdev.domain.codereview;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;

import com.sparta.publicclassdev.domain.codereview.dto.CodeReviewsRequestDto;
import com.sparta.publicclassdev.domain.codereview.entity.CodeReviews;
import com.sparta.publicclassdev.domain.codereview.repository.CodeReviewsRepository;
import com.sparta.publicclassdev.domain.codereview.service.CodeReviewsService;
import com.sparta.publicclassdev.domain.codereviewcomment.repository.CodeReviewCommentsRepository;
import com.sparta.publicclassdev.domain.users.entity.RoleEnum;
import com.sparta.publicclassdev.domain.users.entity.Users;
import com.sparta.publicclassdev.domain.users.repository.UsersRepository;
import com.sparta.publicclassdev.global.aws.AwsS3Util;
import com.sparta.publicclassdev.global.exception.CustomException;
import com.sparta.publicclassdev.global.exception.ErrorCode;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
public class CodeReviewsServiceUnitTest {

  private String testUserName = "testuser";
  private String testUserEmail = "test@email.com";
  private String testUserPassword = "Test123!";
  private RoleEnum testUserRole = RoleEnum.USER;
  private String testCodeReviewTitle = "Title";
  private String testCodeReviewCategory = "#category ";
  private String testCodeReviewContents = "Contents";
  private Long testUserId = 1L;
  private Long testCodeReviewId = 1L;


  @Mock
  private CodeReviewsRepository codeReviewsRepository;

  @Mock
  private CodeReviewCommentsRepository codeReviewCommentsRepository;

  @Mock
  private UsersRepository usersRepository;

  @Mock
  private AwsS3Util awsS3Util;

  @InjectMocks
  private CodeReviewsService codeReviewsService;

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

  private CodeReviewsRequestDto createTestCodeReviewsRequestDto() {
    CodeReviewsRequestDto requestDto = new CodeReviewsRequestDto();

    ReflectionTestUtils.setField(requestDto, "title", testCodeReviewTitle);
    ReflectionTestUtils.setField(requestDto, "contents", testCodeReviewContents);
    ReflectionTestUtils.setField(requestDto, "category", testCodeReviewCategory);
    ReflectionTestUtils.setField(requestDto, "code", "Code");

    return requestDto;
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
      Users validatedUser = codeReviewsService.validateUser(user);

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
        codeReviewsService.validateUser(user);
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
      CodeReviews validatedCodeReview = codeReviewsService.validateCodeReviewId(1L);

      // then
      assertNotNull(validatedCodeReview);
      assertEquals(1L, validatedCodeReview.getId());
    }

    @Test
    void testValidateCodeReviewId_CodeReviewNotFound() {
      // given
      given(codeReviewsRepository.findById(anyLong())).willReturn(Optional.empty());

      // when & then
      CustomException exception = assertThrows(CustomException.class, () -> {
        codeReviewsService.validateCodeReviewId(1L);
      });
      assertEquals(ErrorCode.NOT_FOUND_CODEREVIEW_POST, exception.getErrorCode());
    }

  }

  @Nested
  class ValidateOwnershipTest {

    @Test
    void testValidateOwnership() {
      // given
      Users writer = createTestUser();
      CodeReviews codeReview = createTestCodeReviews(writer);

      // when & then
      codeReviewsService.validateOwnership(codeReview, writer);
    }

    @Test
    void testValidateOwnership_Unauthorized() {
      // given
      Users writer = createTestUser();
      CodeReviews codeReview = createTestCodeReviews(writer);

      testUserId = 2L;
      Users otherUser = createTestUser();

      // when & then
      CustomException exception = assertThrows(CustomException.class, () -> {
        codeReviewsService.validateOwnership(codeReview, otherUser);
      });
      assertEquals(ErrorCode.NOT_UNAUTHORIZED, exception.getErrorCode());
    }

    @Test
    void testValidateOwnership_Admin() {
      // given
      testUserRole = RoleEnum.ADMIN;
      Users writer = createTestUser();
      CodeReviews codeReview = createTestCodeReviews(writer);

      // when & then
      codeReviewsService.validateOwnership(codeReview, writer);
    }

  }

  @Test
  void testArrangeCategory() {
    // given
    String category = "#JAVA #code test # security ";

    // when
    String arrangedCategory = codeReviewsService.arrangeCategory(category);

    // then
    assertEquals("#java #codetest #security ", arrangedCategory);
  }

  @Nested
  class uploadCodeFileTest {

    @Test
    void testUploadCodeFile() throws IOException {
      // given
      String code = createTestCode(testCodeReviewId);
      String filename = "codereviews-code/code-1.txt";

      doNothing().when(awsS3Util).uploadFile(anyString(), any(File.class));

      // when
      String result = codeReviewsService.uploadCodeFile(testCodeReviewId, code);

      // then
      assertEquals(filename, result);
    }

    @Test
    void testUploadCodeFile_UploadFail() throws IOException {
      // given
      String code = createTestCode(testCodeReviewId);
      String filename = "codereviews-code/code-1.txt";

      doThrow(new IOException()).when(awsS3Util).uploadFile(anyString(), any(File.class));

      // when &  then
      CustomException exception = assertThrows(CustomException.class, () -> {
        codeReviewsService.uploadCodeFile(testCodeReviewId, code);
      });
      assertEquals(ErrorCode.FILE_UPLOAD_FAILED, exception.getErrorCode());
    }
  }

  @Nested
  class DownloadCodeFileTest {

    @Test
    void testDownloadCodeFile() throws IOException {
      // given
      Users user = createTestUser();
      CodeReviews codeReviews = createTestCodeReviews(user);
      String filename = createTestCode(testCodeReviewId);
      String downloadedCode = "Test Code {}";
      InputStream mockInputStream = new ByteArrayInputStream(
          downloadedCode.getBytes(StandardCharsets.UTF_8));

      given(awsS3Util.downloadFile(filename)).willReturn(mockInputStream);

      // when
      String result = codeReviewsService.downloadCodeFile(codeReviews);

      // then
      assertEquals(downloadedCode, result);
    }

    @Test
    void testDownloadCodeFile_DownloadFail() throws IOException {
      // given
      Users user = createTestUser();
      CodeReviews codeReviews = createTestCodeReviews(user);
      String filename = createTestCode(testCodeReviewId);
      String downloadedCode = "Test Code {}";

      given(awsS3Util.downloadFile(filename)).willThrow(new IOException());

      // when & then
      CustomException exception = assertThrows(CustomException.class, () -> {
        codeReviewsService.downloadCodeFile(codeReviews);
      });
      assertEquals(ErrorCode.FILE_DOWNLOAD_FAILED, exception.getErrorCode());
    }
  }

}
