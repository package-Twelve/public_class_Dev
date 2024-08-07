package com.sparta.publicclassdev.domain.codereviewcomment;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.publicclassdev.domain.codereview.entity.CodeReviews;
import com.sparta.publicclassdev.domain.codereviewcomment.controller.CodeReviewCommentsController;
import com.sparta.publicclassdev.domain.codereviewcomment.dto.CodeReviewCommentsRequestDto;
import com.sparta.publicclassdev.domain.codereviewcomment.dto.CodeReviewCommentsResponseDto;
import com.sparta.publicclassdev.domain.codereviewcomment.entity.CodeReviewComments;
import com.sparta.publicclassdev.domain.codereviewcomment.service.CodeReviewCommentsService;
import com.sparta.publicclassdev.domain.users.entity.RoleEnum;
import com.sparta.publicclassdev.domain.users.entity.Users;
import com.sparta.publicclassdev.global.config.WebSecurityConfig;
import com.sparta.publicclassdev.global.mvc.MockSpringSecurityFilter;
import com.sparta.publicclassdev.global.security.UserDetailsImpl;
import java.security.Principal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@WebMvcTest(
    controllers = {CodeReviewCommentsController.class},
    excludeFilters = {
        @ComponentScan.Filter(
            type = FilterType.ASSIGNABLE_TYPE,
            classes = WebSecurityConfig.class
        )
    }
)
public class CodeReviewCommentsControllerTest {

  private MockMvc mockMvc;

  private Principal mockPrincipal;

  @Autowired
  private WebApplicationContext context;

  @MockBean
  private CodeReviewCommentsService codeReviewCommentsService;

  @Autowired
  private ObjectMapper objectMapper;

  private CodeReviewCommentsResponseDto codeReviewCommentsResponseDto;
  private CodeReviewCommentsRequestDto codeReviewCommentsRequestDto;

  @BeforeEach
  public void setUp() {
    mockMvc = MockMvcBuilders.webAppContextSetup(context)
        .apply(
            springSecurity(new MockSpringSecurityFilter()))
        .build();
  }

  private void mockUserSetup() {
    Users user = Users.builder()
        .name("testuser")
        .email("test@example.com")
        .password("Test123!")
        .role(RoleEnum.USER)
        .build();

    UserDetailsImpl testUserDetails = new UserDetailsImpl(user);

    mockPrincipal = new UsernamePasswordAuthenticationToken(testUserDetails, "",
        testUserDetails.getAuthorities());
  }

  private void mockSetUp() {
    Users user = Users.builder()
        .name("testuser")
        .email("test@example.com")
        .password("Test123!")
        .role(RoleEnum.USER)
        .build();

    CodeReviews codeReview = CodeReviews.builder()
        .id(1L)
        .title("Test Review")
        .category("#category ")
        .contents("Test contents")
        .status(CodeReviews.Status.ACTIVE)
        .user(user)
        .build();

    CodeReviewComments codeReviewComments = CodeReviewComments.builder()
        .id(1L)
        .contents("Comment")
        .status(CodeReviewComments.Status.ACTIVE)
        .user(user)
        .codeReviews(codeReview)
        .build();

    codeReviewCommentsRequestDto = new CodeReviewCommentsRequestDto();
    ReflectionTestUtils.setField(codeReviewCommentsRequestDto, "contents", "Test Contents");

    codeReviewCommentsResponseDto = new CodeReviewCommentsResponseDto(codeReviewComments, user);

  }

  @Test
  void testCreateCodeReviewComment() throws Exception {
    // given
    this.mockUserSetup();
    this.mockSetUp();

    given(codeReviewCommentsService.createCodeReviewComment(any(), any(), any()))
        .willReturn(codeReviewCommentsResponseDto);

    // when
    MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(
            "/api/codereviews/1/comments")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(codeReviewCommentsRequestDto))
        .principal(mockPrincipal);

    // then
    mockMvc.perform(request)
        .andExpect(MockMvcResultMatchers.status().isCreated())
        .andExpect(MockMvcResultMatchers.jsonPath("$.statusCode").value(201))
        .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("코드 리뷰 댓글 등록 완료"))
        .andExpect(MockMvcResultMatchers.jsonPath("$.data.id").value(1L));
  }

  @Test
  void testUpdateCodeReviewComment() throws Exception {
    // given
    this.mockUserSetup();
    this.mockSetUp();

    given(codeReviewCommentsService.updateCodeReviewComment(any(Long.class), any(Long.class),
        any(CodeReviewCommentsRequestDto.class), any()))
        .willReturn(codeReviewCommentsResponseDto);

    // when
    MockHttpServletRequestBuilder request = MockMvcRequestBuilders.put(
            "/api/codereviews/1/comments/1")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(codeReviewCommentsRequestDto))
        .principal(mockPrincipal);

    // then
    mockMvc.perform(request)
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$.statusCode").value(200))
        .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("코드 리뷰 댓글 수정 완료"))
        .andExpect(MockMvcResultMatchers.jsonPath("$.data.id").value(1L));
  }

  @Test
  void testDeleteCodeReviewComment() throws Exception {
    // given
    this.mockUserSetup();
    this.mockSetUp();

    doNothing().when(codeReviewCommentsService)
        .deleteCodeReviewComment(any(Long.class), any(Long.class), any());

    // when
    MockHttpServletRequestBuilder request = MockMvcRequestBuilders.delete(
            "/api/codereviews/1/comments/1")
        .principal(mockPrincipal);

    // then
    mockMvc.perform(request)
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$.statusCode").value(200))
        .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("코드 리뷰 댓글 삭제 완료"));
  }
}
