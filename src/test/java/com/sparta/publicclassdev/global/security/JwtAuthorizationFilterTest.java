package com.sparta.publicclassdev.global.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.publicclassdev.domain.users.dao.UserRedisDao;
import com.sparta.publicclassdev.domain.users.entity.RoleEnum;
import com.sparta.publicclassdev.domain.users.entity.Users;
import com.sparta.publicclassdev.global.dto.MessageResponse;
import com.sparta.publicclassdev.global.exception.ErrorCode;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;

@ExtendWith(MockitoExtension.class)
public class JwtAuthorizationFilterTest {
    @Mock
    JwtUtil jwtUtil;
    @Mock
    UserDetailsServiceImpl userDetailsService;
    @Mock
    UserRedisDao redisDao;
    JwtAuthorizationFilter filter;
    MockHttpServletRequest request;
    MockHttpServletResponse response;
    private Users user;
    private FilterChain filterChain;

    @Test
    @BeforeEach
    void setUp() {
        filter = new JwtAuthorizationFilter(jwtUtil, userDetailsService, redisDao);
        user = Users.builder()
            .name("kyungtae42")
            .email("test@email.com")
            .password("password")
            .point(0)
            .role(RoleEnum.USER)
            .build();
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        filterChain = Mockito.mock(FilterChain.class);
    }
    @Test
    public void doFilterTest() throws Exception {
        Claims claims = Mockito.mock(Claims.class);
        given(claims.getSubject()).willReturn("test@email.com");

        request.addHeader("Authorization", "Bearer accessToken");
        given(jwtUtil.substringToken(anyString())).willReturn("accessToken");
        given(redisDao.getBlackList(anyString())).willReturn(null);
        given(jwtUtil.validateToken(anyString())).willReturn(true);
        given(jwtUtil.getUserInfoFromToken(anyString())).willReturn(claims);
        given(userDetailsService.loadUserByUsername(anyString())).willReturn(new UserDetailsImpl(user));
        filter.doFilterInternal(request, response, filterChain);

        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        assertEquals(userDetails.getUser(), user);
    }

    @Test
    public void doFilterTestTokenExpiredException() throws Exception {
        request.addHeader("Authorization", "Bearer accessToken");
        given(jwtUtil.substringToken(anyString())).willReturn("accessToken");
        given(redisDao.getBlackList(anyString())).willReturn(null);
        given(jwtUtil.validateToken(anyString())).willReturn(false);
        filter.doFilterInternal(request, response, filterChain);

        ObjectMapper objectMapper = new ObjectMapper();
        MessageResponse messageResponse = objectMapper.readValue(response.getContentAsString(), MessageResponse.class);
        assertEquals(messageResponse.getMessage(), ErrorCode.TOKEN_EXPIRED.getMessage());
    }

    @Test
    public void doFilterTestUserLogoutException() throws Exception {
        request.addHeader("Authorization", "Bearer accessToken");
        given(jwtUtil.substringToken(anyString())).willReturn("accessToken");
        given(redisDao.getBlackList(anyString())).willReturn("logout");
        filter.doFilterInternal(request, response, filterChain);

        ObjectMapper objectMapper = new ObjectMapper();
        MessageResponse messageResponse = objectMapper.readValue(response.getContentAsString(), MessageResponse.class);
        assertEquals(messageResponse.getMessage(), ErrorCode.USER_LOGOUT.getMessage());
    }
}
