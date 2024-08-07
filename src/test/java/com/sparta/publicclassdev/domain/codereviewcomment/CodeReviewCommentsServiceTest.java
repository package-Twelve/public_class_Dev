package com.sparta.publicclassdev.domain.codereviewcomment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.sparta.publicclassdev.domain.codereview.dto.CodeReviewsRequestDto;
import com.sparta.publicclassdev.domain.codereview.entity.CodeReviews;
import com.sparta.publicclassdev.domain.codereview.entity.CodeReviews.Status;
import com.sparta.publicclassdev.domain.codereview.repository.CodeReviewsRepository;
import com.sparta.publicclassdev.domain.codereview.service.CodeReviewsService;
import com.sparta.publicclassdev.domain.codereviewcomment.dto.CodeReviewCommentsRequestDto;
import com.sparta.publicclassdev.domain.codereviewcomment.dto.CodeReviewCommentsResponseDto;
import com.sparta.publicclassdev.domain.codereviewcomment.entity.CodeReviewComments;
import com.sparta.publicclassdev.domain.codereviewcomment.repository.CodeReviewCommentsRepository;
import com.sparta.publicclassdev.domain.codereviewcomment.service.CodeReviewCommentsService;
import com.sparta.publicclassdev.domain.users.entity.RoleEnum;
import com.sparta.publicclassdev.domain.users.entity.Users;
import com.sparta.publicclassdev.domain.users.repository.UsersRepository;
import io.minio.MinioClient;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

@SpringBootTest
@ActiveProfiles("test")
public class CodeReviewCommentsServiceTest {

  private String testUserName = "testuser";
  private String testUserEmail = "test@email.com";
  private String testUserPassword = "Test123!";
  private RoleEnum testUserRole = RoleEnum.USER;
  private String testCodeReviewTitle = "Title";
  private String testCodeReviewCategory = "#category ";
  private String testCodeReviewContents = "Contents";
  private String testCommentContents = "Comment";
  private Long testUserId = 1L;
  private Long testCodeReviewId = 1L;
  private Long testCommentId = 1L;

  @Autowired
  private CodeReviewsService codeReviewsService;

  @Autowired
  private CodeReviewsRepository codeReviewsRepository;

  @Autowired
  private CodeReviewCommentsRepository codeReviewCommentsRepository;

  @Autowired
  private UsersRepository usersRepository;

  @MockBean
  private MinioClient minioClient;
  @Autowired
  private CodeReviewCommentsService codeReviewCommentsService;

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
        .status(Status.ACTIVE)
        .user(user)
        .build();
  }

  private CodeReviewComments createTestCodeReviewComments(CodeReviews codeReview, Users user) {
    return CodeReviewComments.builder()
        .id(testCommentId)
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

  private String createTestCode(Long codeReviewId) {
    return "codereviews-code/code-" + codeReviewId + ".txt";
  }

  @Test
  public void testCreateCodeReviewComment() {
    // given
    Users user = createTestUser();
    usersRepository.save(user);

    CodeReviews codeReview = createTestCodeReviews(user);
    codeReviewsRepository.save(codeReview);

    CodeReviewCommentsRequestDto requestDto = createTestCodeReviewCommentsRequestDto();

    // when
    CodeReviewCommentsResponseDto responseDto = codeReviewCommentsService.createCodeReviewComment(
        codeReview.getId(), requestDto, user);

    // then
    CodeReviewComments createdComment = codeReviewCommentsRepository.findById(responseDto.getId())
        .orElse(null);

    assertNotNull(createdComment);
    assertEquals(testCodeReviewId, createdComment.getId());
    assertEquals(testCommentContents, createdComment.getContents());
    assertEquals(testUserId, createdComment.getUser().getId());
    assertEquals(CodeReviewComments.Status.ACTIVE, createdComment.getStatus());
  }

  @Test
  @Transactional
  public void testUpdateCodeReviewComment() {
    // given
    Users user = createTestUser();
    usersRepository.save(user);

    CodeReviews codeReview = createTestCodeReviews(user);
    codeReviewsRepository.save(codeReview);

    CodeReviewComments comment = createTestCodeReviewComments(codeReview, user);
    codeReviewCommentsRepository.save(comment);

    testCommentContents = "UPDATE Comment";
    CodeReviewCommentsRequestDto updateRequestDto = createTestCodeReviewCommentsRequestDto();

    // when
    CodeReviewCommentsResponseDto responseDto = codeReviewCommentsService.updateCodeReviewComment(
        codeReview.getId(), comment.getId(), updateRequestDto, user);

    // then
    CodeReviewComments updatedComment = codeReviewCommentsRepository.findById(comment.getId())
        .orElse(null);

    assertNotNull(updatedComment);
    assertEquals(testCodeReviewId, updatedComment.getId());
    assertEquals("UPDATE Comment", updatedComment.getContents());
  }

  @Test
  @Transactional
  public void testDeleteCodeReviewComment() {
    // given
    Users user = createTestUser();
    usersRepository.save(user);

    CodeReviews codeReview = createTestCodeReviews(user);
    codeReviewsRepository.save(codeReview);

    CodeReviewComments comment = createTestCodeReviewComments(codeReview, user);
    codeReviewCommentsRepository.save(comment);

    // when
    codeReviewCommentsService.deleteCodeReviewComment(codeReview.getId(), comment.getId(), user);

    // then
    CodeReviewComments deletedComment = codeReviewCommentsRepository.findById(comment.getId())
        .orElse(null);

    assertNotNull(deletedComment);
    assertEquals(testCodeReviewId, deletedComment.getId());
    assertEquals(CodeReviewComments.Status.DELETED, deletedComment.getStatus());
  }
}
