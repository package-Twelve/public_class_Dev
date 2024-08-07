package com.sparta.publicclassdev.domain.codereview;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import com.sparta.publicclassdev.domain.codereview.dto.CodeReviewsListResponseDto;
import com.sparta.publicclassdev.domain.codereview.dto.CodeReviewsRequestDto;
import com.sparta.publicclassdev.domain.codereview.dto.CodeReviewsResponseDto;
import com.sparta.publicclassdev.domain.codereview.dto.CodeReviewsSearchResponseDto;
import com.sparta.publicclassdev.domain.codereview.entity.CodeReviews;
import com.sparta.publicclassdev.domain.codereview.entity.CodeReviews.Status;
import com.sparta.publicclassdev.domain.codereview.repository.CodeReviewsRepository;
import com.sparta.publicclassdev.domain.codereview.service.CodeReviewsService;
import com.sparta.publicclassdev.domain.codereviewcomment.repository.CodeReviewCommentsRepository;
import com.sparta.publicclassdev.domain.users.entity.RoleEnum;
import com.sparta.publicclassdev.domain.users.entity.Users;
import com.sparta.publicclassdev.domain.users.repository.UsersRepository;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.errors.ErrorResponseException;
import io.minio.errors.InsufficientDataException;
import io.minio.errors.InternalException;
import io.minio.errors.InvalidResponseException;
import io.minio.errors.ServerException;
import io.minio.errors.XmlParserException;
import jakarta.transaction.Transactional;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

@SpringBootTest
@ActiveProfiles("test")
public class CodeReviewsServiceTest {

  private String testUserName = "testuser";
  private String testUserEmail = "test@email.com";
  private String testUserPassword = "Test123!";
  private RoleEnum testUserRole = RoleEnum.USER;
  private String testCodeReviewTitle = "Title";
  private String testCodeReviewCategory = "#category ";
  private String testCodeReviewContents = "Contents";
  private Long testUserId = 1L;
  private Long testCodeReviewId = 1L;

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


  @Test
  @Transactional
  void testCreateCodeReview() {
    // Given
    Users user = createTestUser();

    usersRepository.save(user);

    CodeReviewsRequestDto requestDto = createTestCodeReviewsRequestDto();

    try {
      given(minioClient.putObject(any(PutObjectArgs.class))).willReturn(null);
    } catch (Exception e) {
      fail("테스트 실패: MinIO 설정 문제");
    }
    // When
    CodeReviewsResponseDto responseDto = codeReviewsService.createCodeReview(requestDto, user);

    // Then
    assertNotNull(responseDto);
    assertEquals(testCodeReviewTitle, responseDto.getTitle());
    assertEquals(testCodeReviewCategory, responseDto.getCategory());
    assertEquals(testCodeReviewContents, responseDto.getContents());
  }


  @Test
  void testGetAllCodeReviews() {
    // Given
    Users user = createTestUser();
    usersRepository.save(user);

    CodeReviews codeReview = createTestCodeReviews(user);
    codeReviewsRepository.save(codeReview);

    // When
    CodeReviewsListResponseDto responseList = codeReviewsService.getAllCodeReviews(0);

    // Then
    assertNotNull(responseList);
    assertEquals(testCodeReviewId, responseList.getItems().get(0).getId());
    assertEquals(1, responseList.getTotalItems());
  }

  @Test
  public void testGetCodeReviewsByCategory() {
    // given
    Users user = createTestUser();
    usersRepository.save(user);

    CodeReviews codeReview = createTestCodeReviews(user);
    codeReviewsRepository.save(codeReview);

    int page = 0;
    String searchCategory = "category";

    // when
    CodeReviewsSearchResponseDto responseDtoResult = codeReviewsService.getCodeReviewsByCategory(
        searchCategory, page);

    // then
    assertEquals(searchCategory, responseDtoResult.getSearchCategory());
    assertEquals(1, responseDtoResult.getTotalItems());
    assertEquals(codeReview.getId(), responseDtoResult.getItems().get(0).getId());
  }

  @Test
  @Transactional
  public void testDeleteCodeReview() {
    // given
    Users user = createTestUser();
    usersRepository.save(user);

    CodeReviews codeReview = createTestCodeReviews(user);
    codeReviewsRepository.save(codeReview);

    // when
    codeReviewsService.deleteCodeReview(codeReview.getId(), user);

    // then
    CodeReviews deletedCodeReview = codeReviewsRepository.findById(codeReview.getId()).orElse(null);

    assertNotNull(deletedCodeReview);
    assertEquals(Status.DELETED, deletedCodeReview.getStatus());
  }

  @Test
  @Transactional
  public void testUpdateCodeReview()
      throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
    // given
    Users user = createTestUser();
    usersRepository.save(user);

    CodeReviews codeReview = createTestCodeReviews(user);
    codeReviewsRepository.save(codeReview);

    CodeReviewsRequestDto updateRequestDto = createTestCodeReviewsRequestDto();
    ReflectionTestUtils.setField(updateRequestDto, "title", "UPDATE Title");
    ReflectionTestUtils.setField(updateRequestDto, "contents", "UPDATE Contents");
    ReflectionTestUtils.setField(updateRequestDto, "category", "#updatecategory");
    ReflectionTestUtils.setField(updateRequestDto, "code", "UPDATE Code");

    String updatedCodeFileName = createTestCode(codeReview.getId());

    given(minioClient.putObject(any(PutObjectArgs.class))).willAnswer(invocation -> {
      PutObjectArgs args = invocation.getArgument(0);
      assertEquals(updatedCodeFileName, args.object());
      return null;
    });

    // when
    CodeReviewsResponseDto responseDto = codeReviewsService.updateCodeReview(
        updateRequestDto, codeReview.getId(), user);

    // then
    CodeReviews updatedCodeReview = codeReviewsRepository.findById(codeReview.getId())
        .orElse(null);

    assertNotNull(updatedCodeReview);
    assertEquals("UPDATE Title", updatedCodeReview.getTitle());
    assertEquals("UPDATE Contents", updatedCodeReview.getContents());
    assertEquals("#updatecategory ", updatedCodeReview.getCategory());
    assertEquals(updatedCodeFileName, updatedCodeReview.getCode());
  }
}
