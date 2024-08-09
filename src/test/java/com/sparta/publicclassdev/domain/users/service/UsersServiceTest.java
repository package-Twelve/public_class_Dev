package com.sparta.publicclassdev.domain.users.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

import com.sparta.publicclassdev.domain.communities.repository.CommunitiesRepository;
import com.sparta.publicclassdev.domain.users.dao.UserRedisDao;
import com.sparta.publicclassdev.domain.users.dto.AuthRequestDto;
import com.sparta.publicclassdev.domain.users.dto.SignupRequestDto;
import com.sparta.publicclassdev.domain.users.dto.SignupResponseDto;
import com.sparta.publicclassdev.domain.users.entity.RoleEnum;
import com.sparta.publicclassdev.domain.users.entity.Users;
import com.sparta.publicclassdev.domain.users.repository.UsersRepository;
import com.sparta.publicclassdev.global.security.JwtUtil;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class UsersServiceTest {
    private final String ADMIN_TOKEN = "test1234!";
    private String testName = "testuser";
    private String testDuplicateName = "testuser1";
    private String testEmail = "test@email.com";
    private String testDuplicateEmail = "test1@email.com";
    private String testPassword = "Asdf1234!";
    private RoleEnum testUserRole = RoleEnum.USER;
    private RoleEnum testAdminRole = RoleEnum.USER;
    Users testDuplicateUser = Users.builder()
        .name(testDuplicateName)
        .email(testDuplicateEmail)
        .password(testPassword)
        .point(0)
        .role(testUserRole)
        .build();
    @Mock
    UsersRepository usersRepository;
    @Mock
    CommunitiesRepository communitiesRepository;
    @Spy
    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    @Mock
    JwtUtil jwtUtil;
    @Mock
    UserRedisDao redisDao;

    UsersService usersService;
    private SignupRequestDto createTestSignupRequestDto() {
        SignupRequestDto requestDto = new SignupRequestDto();

        ReflectionTestUtils.setField(requestDto, "name", testName);
        ReflectionTestUtils.setField(requestDto, "email", testEmail);
        ReflectionTestUtils.setField(requestDto, "password", testPassword);

        return requestDto;
    }

    private SignupRequestDto createTestAdminSignupRequestDto() {
        SignupRequestDto requestDto = new SignupRequestDto();

        ReflectionTestUtils.setField(requestDto, "name", testName);
        ReflectionTestUtils.setField(requestDto, "email", testEmail);
        ReflectionTestUtils.setField(requestDto, "password", testPassword);
        ReflectionTestUtils.setField(requestDto, "admin", true);
        ReflectionTestUtils.setField(requestDto, "adminToken", ADMIN_TOKEN);

        return requestDto;
    }


    @Test
    @BeforeEach
    void setUp() {
        this.usersService = new UsersService(usersRepository
            , communitiesRepository
            , passwordEncoder
            , jwtUtil
            , redisDao);
    }

    @Test
    void createUserTest() {
        SignupRequestDto requestDto = createTestSignupRequestDto();
        given(usersRepository.findByEmail(testEmail)).willReturn(Optional.empty());
        given(usersRepository.findByName(testName)).willReturn(Optional.empty());
        SignupResponseDto responseDto = usersService.createUser(requestDto);

        assertNotNull(responseDto);
        assertEquals(testName, responseDto.getName());
        assertEquals(testEmail, responseDto.getEmail());
        assertEquals(testUserRole, responseDto.getRole());
    }

    @Test
    void createAdminTest() {
        SignupRequestDto requestDto = createTestSignupRequestDto();
        given(usersRepository.findByEmail(testEmail)).willReturn(Optional.empty());
        given(usersRepository.findByName(testName)).willReturn(Optional.empty());
        SignupResponseDto responseDto = usersService.createUser(requestDto);

        assertNotNull(responseDto);
        assertEquals(testName, responseDto.getName());
        assertEquals(testEmail, responseDto.getEmail());
        assertEquals(testAdminRole, responseDto.getRole());
    }

    @Test
    void login() {
    }

    @Test
    void getProfile() {
    }

    @Test
    void updateProfile() {
    }

    @Test
    void findById() {
    }

    @Test
    void logout() {
    }

    @Test
    void withdraw() {
    }

    @Test
    void reissueToken() {
    }

    @Test
    void reissueTokenWithEmail() {
    }

    @Test
    void getPoint() {
    }

    @Test
    void updatePoint() {
    }

    @Test
    void updatePassword() {
    }
}