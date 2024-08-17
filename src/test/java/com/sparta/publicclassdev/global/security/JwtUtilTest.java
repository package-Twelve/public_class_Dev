package com.sparta.publicclassdev.global.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.sparta.publicclassdev.domain.users.entity.RoleEnum;
import com.sparta.publicclassdev.domain.users.entity.Users;
import com.sparta.publicclassdev.global.exception.CustomException;
import com.sparta.publicclassdev.global.exception.ErrorCode;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.security.Key;
import java.util.Date;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.mockito.Spy;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(value = MethodOrderer.OrderAnnotation.class)
public class JwtUtilTest {
    private static JwtUtil jwtUtil;
    private String secretKey;
    private Users user;
    private String accessToken;
    private String refreshToken;
    @Spy
    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @BeforeEach
    void setUp() {
        user = Users.builder()
            .name("testName")
            .email("testemail@email.com")
            .password(passwordEncoder.encode("Asdf1234!"))
            .point(0)
            .role(RoleEnum.USER)
            .build();
        jwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(jwtUtil, "secretKey", "dGVzdHNlY3JldGtleXRlc3RzZWNyZXRrZXl0ZXN0c2VjcmV0a2V5dGVzdHNlY3JldGtleXRlc3RzZWNyZXRrZXk=");
        jwtUtil.init();
    }

    @Test
    @Order(1)
    void createAccessToken() {
        this.accessToken =  jwtUtil.createAccessToken(user);
        assertNotNull(this.accessToken);
    }
    @Test
    @Order(2)
    void createRefreshToken() {
        this.refreshToken =  jwtUtil.createRefreshToken(user);
        assertNotNull(this.refreshToken);
    }
    @Test
    void getJwtFromHeader() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(JwtUtil.AUTHORIZATION_HEADER, this.accessToken);
        String substringToken = jwtUtil.getJwtFromHeader(request);
        assertFalse(substringToken.contains("Bearer"));
    }
    @Test
    void substringToken() {
        String substringToken = jwtUtil.substringToken(this.accessToken);
        assertFalse(substringToken.contains("Bearer"));
    }
    @Test
    void substringTokenTokenNotFoundException() {
        String substringToken = jwtUtil.substringToken(this.accessToken);
        Throwable exception = assertThrows(CustomException.class, () -> jwtUtil.substringToken(substringToken));
        assertEquals(ErrorCode.TOKEN_NOTFOUND.getMessage(), exception.getMessage());
    }
    @Test
    void validateToken() {
        String substringToken = jwtUtil.substringToken(this.accessToken);
        assertTrue(jwtUtil.validateToken(substringToken));
    }
    @Test
    void validateTokenException() throws InterruptedException {
        Date date = new Date();
        Key key = (Key) ReflectionTestUtils.getField(jwtUtil, "key");
        String substringToken = Jwts.builder()
            .setSubject(user.getEmail())
            .claim("auth", user.getRole())
            .setExpiration(new Date(date.getTime() + 1000L))
            .setIssuedAt(date)
            .signWith(key, SignatureAlgorithm.HS256)
            .compact();
        Thread.sleep(2000L);
        assertFalse(jwtUtil.validateToken(substringToken));
        ReflectionTestUtils.setField(jwtUtil, "ACCESSTOKEN_TIME", 60 * 30 * 1000L);
    }
    @Test
    void getUserInfoFromToken() {
        String substringToken = jwtUtil.substringToken(this.accessToken);
        Claims claims = jwtUtil.getUserInfoFromToken(substringToken);
        assertEquals(claims.getSubject(), user.getEmail());
    }
    @Test
    void getExpiration() {
        String substringToken = jwtUtil.substringToken(this.accessToken);
        Long expiration = jwtUtil.getExpiration(substringToken);
        assertNotNull(expiration);
    }
}
