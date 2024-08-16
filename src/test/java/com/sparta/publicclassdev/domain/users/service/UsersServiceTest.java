package com.sparta.publicclassdev.domain.users.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

import com.sparta.publicclassdev.domain.communities.repository.CommunitiesRepository;
import com.sparta.publicclassdev.domain.users.dao.UserRedisDao;
import com.sparta.publicclassdev.domain.users.dto.AuthRequestDto;
import com.sparta.publicclassdev.domain.users.dto.AuthResponseDto;
import com.sparta.publicclassdev.domain.users.dto.PasswordRequestDto;
import com.sparta.publicclassdev.domain.users.dto.PointResponseDto;
import com.sparta.publicclassdev.domain.users.dto.ProfileRequestDto;
import com.sparta.publicclassdev.domain.users.dto.ProfileResponseDto;
import com.sparta.publicclassdev.domain.users.dto.SignupRequestDto;
import com.sparta.publicclassdev.domain.users.dto.SignupResponseDto;
import com.sparta.publicclassdev.domain.users.dto.UpdateProfileResponseDto;
import com.sparta.publicclassdev.domain.users.entity.CalculateTypeEnum;
import com.sparta.publicclassdev.domain.users.entity.RankEnum;
import com.sparta.publicclassdev.domain.users.entity.RoleEnum;
import com.sparta.publicclassdev.domain.users.entity.Users;
import com.sparta.publicclassdev.domain.users.repository.UsersRepository;
import com.sparta.publicclassdev.global.exception.CustomException;
import com.sparta.publicclassdev.global.exception.ErrorCode;
import com.sparta.publicclassdev.global.security.JwtUtil;
import java.util.ArrayList;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class UsersServiceTest {
    private final String ADMIN_TOKEN = "test1234!";
    private String testName = "kyungtae42";
    private String modifiedTestName = "modifiedkyungtae42";
    private String testDuplicateName = "testuser1";
    private String testEmail = "test@email.com";
    private String testDuplicateEmail = "test1@email.com";
    private String testPassword = "Asdf1234!";
    private String modifiedTestPassword = "Password1234!";
    private String intro = "myintro";
    private RoleEnum testUserRole = RoleEnum.USER;
    private RoleEnum testAdminRole = RoleEnum.ADMIN;
    Users testUser;
    Users testDuplicateUser;
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
    private AuthRequestDto createTestAuthRequestDto() {
        AuthRequestDto requestDto = new AuthRequestDto();

        ReflectionTestUtils.setField(requestDto, "email", testEmail);
        ReflectionTestUtils.setField(requestDto, "password", testPassword);

        return requestDto;
    }
    private ProfileRequestDto createTestProfileRequestDto() {
        ProfileRequestDto requestDto = new ProfileRequestDto();

        ReflectionTestUtils.setField(requestDto, "name", modifiedTestName);
        ReflectionTestUtils.setField(requestDto, "intro", intro);

        return requestDto;
    }
    private PasswordRequestDto createTestPasswordRequestDto() {
        PasswordRequestDto requestDto = new PasswordRequestDto();

        ReflectionTestUtils.setField(requestDto, "password", modifiedTestPassword);

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
        this.testUser = Users.builder()
            .name(testName)
            .email(testEmail)
            .password(passwordEncoder.encode(testPassword))
            .point(0)
            .role(testUserRole)
            .build();
        this.testDuplicateUser = Users.builder()
            .name(testDuplicateName)
            .email(testDuplicateEmail)
            .password(passwordEncoder.encode(testPassword))
            .point(0)
            .role(testUserRole)
            .build();
        ReflectionTestUtils.setField(testUser, "id", 1L);
        ReflectionTestUtils.setField(testDuplicateUser, "id", 2L);
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
        SignupRequestDto requestDto = createTestAdminSignupRequestDto();
        given(usersRepository.findByEmail(testEmail)).willReturn(Optional.empty());
        given(usersRepository.findByName(testName)).willReturn(Optional.empty());
        ReflectionTestUtils.setField(usersService, "ADMIN_TOKEN", ADMIN_TOKEN);
        SignupResponseDto responseDto = usersService.createUser(requestDto);

        assertNotNull(responseDto);
        assertEquals(testName, responseDto.getName());
        assertEquals(testEmail, responseDto.getEmail());
        assertEquals(testAdminRole, responseDto.getRole());
    }
    @Test
    void createUserUserNotUniqueTest() {
        SignupRequestDto requestDto = createTestSignupRequestDto();
        given(usersRepository.findByEmail(testEmail)).willReturn(Optional.of(testUser));

        Throwable exception = assertThrows(CustomException.class, () -> usersService.createUser(requestDto));
        assertEquals(ErrorCode.USER_NOT_UNIQUE.getMessage(), exception.getMessage());
    }
    @Test
    void createUserNameNotUniqueTest() {
        SignupRequestDto requestDto = createTestSignupRequestDto();
        given(usersRepository.findByEmail(testEmail)).willReturn(Optional.empty());
        given(usersRepository.findByName(testName)).willReturn(Optional.of(testUser));

        Throwable exception = assertThrows(CustomException.class, () -> usersService.createUser(requestDto));
        assertEquals(ErrorCode.NAME_NOT_UNIQUE.getMessage(), exception.getMessage());
    }
    @Test
    void createAdminIncorrectAdminTokenTest() {
        SignupRequestDto requestDto = createTestAdminSignupRequestDto();
        ReflectionTestUtils.setField(requestDto, "adminToken", ADMIN_TOKEN + "incorrect");
        ReflectionTestUtils.setField(usersService, "ADMIN_TOKEN", ADMIN_TOKEN);

        Throwable exception = assertThrows(CustomException.class, () -> usersService.createUser(requestDto));
        assertEquals(ErrorCode.INCORRECT_MANAGER_KEY.getMessage(), exception.getMessage());
    }
    @Test
    void login() {
        AuthRequestDto requestDto = createTestAuthRequestDto();
        given(usersRepository.findByEmail(anyString())).willReturn(Optional.of(testUser));
        given(jwtUtil.createAccessToken(any(Users.class))).willReturn("Bearer access" + testEmail);
        given(jwtUtil.createRefreshToken(any(Users.class))).willReturn("Bearer refresh" + testEmail);
        given(jwtUtil.getREFRESHTOKEN_TIME()).willReturn(60 * 60 * 1000L * 336);
        AuthResponseDto responseDto = usersService.login(requestDto);

        assertNotNull(responseDto);
        assertNotNull(responseDto.getAccessToken());
        assertNotNull(responseDto.getRefreshToken());
    }
    @Test
    void loginCheckEmailException() {
        AuthRequestDto requestDto = createTestAuthRequestDto();
        given(usersRepository.findByEmail(anyString())).willReturn(Optional.empty());

        Throwable exception = assertThrows(CustomException.class, () -> usersService.login(requestDto));
        assertEquals(ErrorCode.CHECK_EMAIL.getMessage(), exception.getMessage());
    }
    @Test
    void loginIncorrectPasswordException() {
        AuthRequestDto requestDto = createTestAuthRequestDto();
        ReflectionTestUtils.setField(requestDto, "password", testPassword + "incorrect");
        given(usersRepository.findByEmail(anyString())).willReturn(Optional.of(testUser));

        Throwable exception = assertThrows(CustomException.class, () -> usersService.login(requestDto));
        assertEquals(ErrorCode.INCORRECT_PASSWORD.getMessage(), exception.getMessage());
    }
    @Test
    void loginUserWithdrawException() {
        AuthRequestDto requestDto = createTestAuthRequestDto();
        ReflectionTestUtils.setField(testUser, "role", RoleEnum.WITHDRAW);
        given(usersRepository.findByEmail(anyString())).willReturn(Optional.of(testUser));

        Throwable exception = assertThrows(CustomException.class, () -> usersService.login(requestDto));
        assertEquals(ErrorCode.USER_WITHDRAW.getMessage(), exception.getMessage());
    }
    @Test
    void getProfile() {
        ProfileResponseDto responseDto = usersService.getProfile(testUser);
        given(communitiesRepository.findPostByUserLimit5(any(Users.class))).willReturn(new ArrayList<>());

        assertNotNull(responseDto);
        assertEquals(responseDto.getName(), testName);
        assertEquals(responseDto.getEmail(), testEmail);
        assertEquals(responseDto.getRole(), testUserRole);
    }
    @Test
    void findByIdUserNotFoundException() {
        given(usersRepository.findById(anyLong())).willReturn(Optional.empty());

        Throwable exception = assertThrows(CustomException.class, () -> usersService.findById(testUser.getId()));
        assertEquals(ErrorCode.USER_NOT_FOUND.getMessage(), exception.getMessage());
    }
    @Test
    void updateProfile() {
        ProfileRequestDto requestDto = createTestProfileRequestDto();
        given(usersRepository.findById(anyLong())).willReturn(Optional.of(testUser));

        UpdateProfileResponseDto responseDto = usersService.updateProfile(testUser.getId(), requestDto);

        assertNotNull(responseDto);
        assertEquals(responseDto.getName(), requestDto.getName());
        assertEquals(responseDto.getIntro(), requestDto.getIntro());
    }
    @Test
    void updatePassword() {
        PasswordRequestDto requestDto = createTestPasswordRequestDto();
        given(usersRepository.findById(anyLong())).willReturn(Optional.of(testUser));

        usersService.updatePassword(testUser.getId(), requestDto);

        assertTrue(passwordEncoder.matches(requestDto.getPassword(), testUser.getPassword()));
    }
    @Test
    void getPoint() {
        given(usersRepository.findById(anyLong())).willReturn(Optional.of(testUser));

        PointResponseDto responseDto = usersService.getPoint(testUser.getId());

        assertEquals(responseDto.getPoint(), testUser.getPoint());
        assertEquals(responseDto.getRank(), RankEnum.getRankByPoints(testUser.getPoint()));
    }

    @Test
    void addPoint() {
        given(usersRepository.findById(anyLong())).willReturn(Optional.of(testUser));
        int oldPoint = testUser.getPoint();
        int addedPoint = 10;
        PointResponseDto responseDto = usersService.updatePoint(testUser.getId(), addedPoint, CalculateTypeEnum.ADD);

        assertEquals(responseDto.getPoint(), oldPoint + addedPoint);
        assertEquals(responseDto.getRank(), RankEnum.getRankByPoints(testUser.getPoint()));
    }
    @Test
    void subtractPoint() {
        addPoint();
        given(usersRepository.findById(anyLong())).willReturn(Optional.of(testUser));
        int oldPoint = testUser.getPoint();
        int subtractedPoint = 10;
        PointResponseDto responseDto = usersService.updatePoint(testUser.getId(), subtractedPoint, CalculateTypeEnum.SUBTRACT);

        assertEquals(responseDto.getPoint(), oldPoint - subtractedPoint);
        assertEquals(responseDto.getRank(), RankEnum.getRankByPoints(testUser.getPoint()));
    }
    @Test
    void reissueToken() {
        given(jwtUtil.substringToken(anyString())).willReturn("refresh");
        given(usersRepository.findByEmail(anyString())).willReturn(Optional.of(testUser));
        given(redisDao.getRefreshToken(anyString())).willReturn("refresh");
        given(jwtUtil.createAccessToken(any(Users.class))).willReturn("Bearer newAccess");
        given(jwtUtil.createRefreshToken(any(Users.class))).willReturn("Bearer newRefresh");
        given(jwtUtil.getREFRESHTOKEN_TIME()).willReturn(60 * 60 * 1000L * 336);

        AuthResponseDto responseDto = usersService.reissueToken("refresh", testEmail);
        assertNotNull(responseDto.getAccessToken());
        assertNotNull(responseDto.getRefreshToken());
    }
    @Test
    void reissueTokenNotFoundEmailException() {
        given(jwtUtil.substringToken(anyString())).willReturn("refresh");
        given(usersRepository.findByEmail(anyString())).willReturn(Optional.empty());

        Throwable exception = assertThrows(UsernameNotFoundException.class, () -> usersService.reissueToken("refresh", testEmail));
        assertEquals("Not Found " + testEmail, exception.getMessage());
    }
    @Test
    void reissueTokenTokenMismatchException() {
        given(jwtUtil.substringToken(anyString())).willReturn("refresh");
        given(usersRepository.findByEmail(anyString())).willReturn(Optional.of(testUser));
        given(redisDao.getRefreshToken(anyString())).willReturn("refresh");

        Throwable exception = assertThrows(CustomException.class, () -> usersService.reissueToken("incorrectrefresh", testEmail));
        assertEquals(ErrorCode.TOKEN_MISMATCH.getMessage(), exception.getMessage());
    }
    @Test
    void logout() {
        given(jwtUtil.substringToken(anyString())).willReturn("access");
        given(jwtUtil.getExpiration(anyString())).willReturn(1775011L);
        given(redisDao.hasKey(anyString())).willReturn(true);

        usersService.logout("Bearer access", testEmail);
    }
    @Test
    void logoutAlreadyLogoutException() {
        given(jwtUtil.substringToken(anyString())).willReturn("access");
        given(jwtUtil.getExpiration(anyString())).willReturn(1775011L);
        given(redisDao.hasKey(anyString())).willReturn(false);

        Throwable exception = assertThrows(CustomException.class, () -> usersService.logout("Bearer access", testEmail));
        assertEquals(ErrorCode.USER_LOGOUT.getMessage(), exception.getMessage());
    }
    @Test
    void withdraw() {
        AuthRequestDto requestDto = createTestAuthRequestDto();
        given(usersRepository.findById(anyLong())).willReturn(Optional.of(testUser));
        usersService.withdraw(testUser.getId(), requestDto);

        assertEquals(testUser.getRole(), RoleEnum.WITHDRAW);
    }
    @Test
    void withdrawCheckEmailException() {
        AuthRequestDto requestDto = createTestAuthRequestDto();
        ReflectionTestUtils.setField(requestDto, "email", "incorrect" + testEmail);
        given(usersRepository.findById(anyLong())).willReturn(Optional.of(testUser));

        Throwable exception = assertThrows(CustomException.class, () -> usersService.withdraw(testUser.getId(), requestDto));
        assertEquals(ErrorCode.CHECK_EMAIL.getMessage(), exception.getMessage());
    }
    @Test
    void withdrawIncorrectPasswordException() {
        AuthRequestDto requestDto = createTestAuthRequestDto();
        ReflectionTestUtils.setField(requestDto, "password", "incorrect" + testPassword);
        given(usersRepository.findById(anyLong())).willReturn(Optional.of(testUser));

        Throwable exception = assertThrows(CustomException.class, () -> usersService.withdraw(testUser.getId(), requestDto));
        assertEquals(ErrorCode.INCORRECT_PASSWORD.getMessage(), exception.getMessage());
    }
    @Test
    void withdrawUserWithdrawException() {
        AuthRequestDto requestDto = createTestAuthRequestDto();
        ReflectionTestUtils.setField(testUser, "role", RoleEnum.WITHDRAW);
        given(usersRepository.findById(anyLong())).willReturn(Optional.of(testUser));

        Throwable exception = assertThrows(CustomException.class, () -> usersService.withdraw(testUser.getId(), requestDto));
        assertEquals(ErrorCode.USER_WITHDRAW.getMessage(), exception.getMessage());
    }
}