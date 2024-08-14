package com.sparta.publicclassdev.domain.likes;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.publicclassdev.domain.likes.controller.LikesController;
import com.sparta.publicclassdev.domain.likes.service.LikesService;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@WebMvcTest(
    controllers = {LikesController.class},
    excludeFilters = {
        @ComponentScan.Filter(
            type = FilterType.ASSIGNABLE_TYPE,
            classes = WebSecurityConfig.class
        )
    }
)
public class LikesControllerTest {

  private MockMvc mockMvc;

  private Principal mockPrincipal;

  @Autowired
  private WebApplicationContext context;

  @MockBean
  private LikesService likesService;

  @Autowired
  private ObjectMapper objectMapper;

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

  @Test
  void testSetLike() throws Exception {
    // given
    this.mockUserSetup();

    String responseMessage = "코드 리뷰 댓글 좋아요 추가 완료";
    given(likesService.setLike(any(Long.class), any(Long.class), any())).willReturn(
        responseMessage);

    // when
    MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(
            "/api/codereviews/1/comments/1/like")
        .contentType(MediaType.APPLICATION_JSON)
        .principal(mockPrincipal);

    // then
    mockMvc.perform(request)
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$.statusCode").value(200))
        .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(responseMessage));
  }
}
