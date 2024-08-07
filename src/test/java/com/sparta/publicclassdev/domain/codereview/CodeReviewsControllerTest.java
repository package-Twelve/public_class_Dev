package com.sparta.publicclassdev.domain.codereview;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.publicclassdev.domain.codereview.controller.CodeReviewsController;
import com.sparta.publicclassdev.domain.codereview.dto.CodeReviewsDetailResponseDto;
import com.sparta.publicclassdev.domain.codereview.dto.CodeReviewsRequestDto;
import com.sparta.publicclassdev.domain.codereview.dto.CodeReviewsResponseDto;
import com.sparta.publicclassdev.domain.codereview.entity.CodeReviews;
import com.sparta.publicclassdev.domain.codereview.service.CodeReviewsService;
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
    controllers = {CodeReviewsController.class},
    excludeFilters = {
        @ComponentScan.Filter(
            type = FilterType.ASSIGNABLE_TYPE,
            classes = WebSecurityConfig.class
        )
    }
)
class CodeReviewsControllerTest {

  private MockMvc mockMvc;

  private Principal mockPrincipal;

  @Autowired
  private WebApplicationContext context;

  @MockBean
  private CodeReviewsService codeReviewsService;

  @Autowired
  private ObjectMapper objectMapper;

  private CodeReviewsRequestDto codeReviewsRequestDto;
  private CodeReviewsResponseDto codeReviewsResponseDto;
  private CodeReviewsDetailResponseDto codeReviewsDetailResponseDto;


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

    codeReviewsRequestDto = new CodeReviewsRequestDto();
    ReflectionTestUtils.setField(codeReviewsRequestDto, "title", "Test Title");
    ReflectionTestUtils.setField(codeReviewsRequestDto, "category", "#category ");
    ReflectionTestUtils.setField(codeReviewsRequestDto, "contents", "Test Contents");

    codeReviewsResponseDto = new CodeReviewsResponseDto(codeReview, user);
  }

  @Test
  void testCreateCodeReview() throws Exception {
    // given
    this.mockUserSetup();
    this.mockSetUp();

    given(codeReviewsService.createCodeReview(any(), any())).willReturn(codeReviewsResponseDto);

    // when
    MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post("/api/codereviews")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(codeReviewsRequestDto))
        .principal(mockPrincipal);

    // then
    mockMvc.perform(request)
        .andExpect(status().isCreated())
        .andExpect(MockMvcResultMatchers.jsonPath("$.statusCode").value(201))
        .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("코드 리뷰 게시글 등록 완료"))
        .andExpect(MockMvcResultMatchers.jsonPath("$.data.id").value(1L));
  }

  @Test
  void testGetAllCodeReviews() throws Exception {
    // given
    this.mockUserSetup();
    this.mockSetUp();

    // when
    MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get("/api/codereviews")
        .param("page", "1");

    // then
    mockMvc.perform(request)
        .andExpect(status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$.statusCode").value(200))
        .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("코드 리뷰 게시글 조회 완료"));
  }

  @Test
  void testGetCodeReview() throws Exception {
    // given
    this.mockUserSetup();
    this.mockSetUp();
    given(codeReviewsService.getCodeReview(1L)).willReturn(codeReviewsDetailResponseDto);

    // when
    MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get(
            "/api/codereviews/{codeReviewsId}", 1L)
        .principal(mockPrincipal);

    // then
    mockMvc.perform(request)
        .andExpect(status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$.statusCode").value(200))
        .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("코드 리뷰 게시글 조회 완료"));
  }

  @Test
  void testGetCodeReviewsByCategory() throws Exception {
    // given
    this.mockUserSetup();
    this.mockSetUp();

    // when
    MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get("/api/codereviews/search")
        .param("category", "#test")
        .param("page", "1")
        .principal(mockPrincipal);

    // then
    mockMvc.perform(request)
        .andExpect(status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$.statusCode").value(200))
        .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("코드 리뷰 게시글 검색 완료"));
  }

  @Test
  void testDeleteCodeReview() throws Exception {
    // given
    this.mockUserSetup();
    this.mockSetUp();
    doNothing().when(codeReviewsService).deleteCodeReview(any(Long.class), any());

    // when
    MockHttpServletRequestBuilder request = MockMvcRequestBuilders.delete(
            "/api/codereviews/{codeReviewsId}", 1L)
        .principal(mockPrincipal);

    // then
    mockMvc.perform(request)
        .andExpect(status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$.statusCode").value(200))
        .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("코드 리뷰 게시글 삭제 완료"));
  }

  @Test
  void testUpdateCodeReview() throws Exception {
    // given
    this.mockUserSetup();
    this.mockSetUp();
    given(codeReviewsService.updateCodeReview(any(), any(Long.class), any())).willReturn(
        codeReviewsResponseDto);

    // when
    MockHttpServletRequestBuilder request = MockMvcRequestBuilders.patch(
            "/api/codereviews/{codeReviewsId}", 1L)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(codeReviewsRequestDto))
        .principal(mockPrincipal);

    // then
    mockMvc.perform(request)
        .andExpect(status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$.statusCode").value(200))
        .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("코드 리뷰 게시글 수정 완료"));
  }
}

