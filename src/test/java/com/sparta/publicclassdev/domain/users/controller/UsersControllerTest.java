package com.sparta.publicclassdev.domain.users.controller;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.matchesPattern;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.publicclassdev.domain.users.dto.AuthRequestDto;
import com.sparta.publicclassdev.domain.users.dto.AuthResponseDto;
import com.sparta.publicclassdev.domain.users.dto.PasswordRequestDto;
import com.sparta.publicclassdev.domain.users.dto.PointRequestDto;
import com.sparta.publicclassdev.domain.users.dto.ProfileRequestDto;
import com.sparta.publicclassdev.domain.users.dto.ReissueTokenRequestDto;
import com.sparta.publicclassdev.domain.users.dto.SignupRequestDto;
import com.sparta.publicclassdev.domain.users.entity.CalculateTypeEnum;
import com.sparta.publicclassdev.domain.users.entity.RoleEnum;
import com.sparta.publicclassdev.domain.users.entity.Users;
import com.sparta.publicclassdev.domain.users.repository.UsersRepository;
import com.sparta.publicclassdev.domain.users.service.UsersService;
import com.sparta.publicclassdev.global.dto.DataResponse;
import com.sparta.publicclassdev.global.exception.CustomException;
import java.io.DataInput;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@TestInstance(Lifecycle.PER_CLASS)
@TestMethodOrder(value = MethodOrderer.OrderAnnotation.class)
class UsersControllerTest {
    @Value("${ADMIN_TOKEN}")
    private String ADMIN_TOKEN;
    private String testName = "testuser";
    private String testName1 = "testuser1";
    private String modifiedTestName = "modifiedtestuser";
    private String testEmail = "test@email.com";
    private String testEmail1 = "test1@email.com";
    private String testPassword = "Asdf1234!";
    private String modifiedTestPassword = "Password1234!";
    private String intro = "myintro";
    private RoleEnum testUserRole = RoleEnum.USER;
    private RoleEnum testAdminRole = RoleEnum.ADMIN;
    private int point = 10;
    private CalculateTypeEnum addType = CalculateTypeEnum.ADD;
    private CalculateTypeEnum subtractType = CalculateTypeEnum.SUBTRACT;
    private String accessToken;
    private String refreshToken;
    private String newAccessToken;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private UsersService usersService;
    @Autowired
    private UsersRepository usersRepository;

    private SignupRequestDto createTestSignupRequestDto() {
        SignupRequestDto requestDto = new SignupRequestDto();

        ReflectionTestUtils.setField(requestDto, "name", testName);
        ReflectionTestUtils.setField(requestDto, "email", testEmail);
        ReflectionTestUtils.setField(requestDto, "password", testPassword);

        return requestDto;
    }

    private SignupRequestDto createTestAdminSignupRequestDto() {
        SignupRequestDto requestDto = new SignupRequestDto();

        ReflectionTestUtils.setField(requestDto, "name", testName1);
        ReflectionTestUtils.setField(requestDto, "email", testEmail1);
        ReflectionTestUtils.setField(requestDto, "password", testPassword);
        ReflectionTestUtils.setField(requestDto, "admin", true);
        ReflectionTestUtils.setField(requestDto, "adminToken", ADMIN_TOKEN);

        return requestDto;
    }

    private ProfileRequestDto createTestProfileRequestDto() {
        ProfileRequestDto requestDto = new ProfileRequestDto();

        ReflectionTestUtils.setField(requestDto, "name", modifiedTestName);
        ReflectionTestUtils.setField(requestDto, "intro", intro);

        return requestDto;
    }

    private AuthRequestDto createTestAuthRequestDto() {
        AuthRequestDto requestDto = new AuthRequestDto();

        ReflectionTestUtils.setField(requestDto, "email", testEmail);
        ReflectionTestUtils.setField(requestDto, "password", testPassword);

        return requestDto;
    }
    private AuthRequestDto createTestReAuthRequestDto() {
        AuthRequestDto requestDto = new AuthRequestDto();

        ReflectionTestUtils.setField(requestDto, "email", testEmail);
        ReflectionTestUtils.setField(requestDto, "password", modifiedTestPassword);

        return requestDto;
    }
    private PasswordRequestDto createTestPasswordRequestDto() {
        PasswordRequestDto requestDto = new PasswordRequestDto();

        ReflectionTestUtils.setField(requestDto, "password", modifiedTestPassword);

        return requestDto;
    }
    private PointRequestDto createTestAddPointRequestDto() {
        PointRequestDto requestDto = new PointRequestDto();

        ReflectionTestUtils.setField(requestDto, "point", 10);
        ReflectionTestUtils.setField(requestDto, "type", addType);

        return requestDto;
    }
    private ReissueTokenRequestDto createTestReissueTokenRequestDto() {
        ReissueTokenRequestDto requestDto = new ReissueTokenRequestDto();

        ReflectionTestUtils.setField(requestDto, "refreshToken", this.refreshToken);

        return requestDto;
    }
    @Test
    @Order(1)
    void createUser() throws Exception{
        SignupRequestDto requestDto = createTestSignupRequestDto();

        ResultActions resultActions = mockMvc.perform(post("/api/users/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto))
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print());

        resultActions
            .andExpectAll(status().isCreated(),
                jsonPath("data.name").value(requestDto.getName()),
                jsonPath("data.email").value(requestDto.getEmail()),
                jsonPath("data.role").value(testUserRole.toString()));
    }

    @Test
    @Order(2)
    void createAdminUser() throws Exception{
        SignupRequestDto requestDto = createTestAdminSignupRequestDto();

        ResultActions resultActions = mockMvc.perform(post("/api/users/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto))
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print());

        resultActions
            .andExpectAll(status().isCreated(),
                jsonPath("data.name").value(requestDto.getName()),
                jsonPath("data.email").value(requestDto.getEmail()),
                jsonPath("data.role").value(testAdminRole.toString()));
    }

    @Test
    @Order(3)
    void login() throws Exception{
        AuthRequestDto requestDto = createTestAuthRequestDto();
        ResultActions resultActions = mockMvc.perform(post("/api/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto))
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print());

        MvcResult result = resultActions
            .andExpectAll(status().isOk(),
                jsonPath("data.accessToken", is(notNullValue())),
                jsonPath("data.refreshToken", is(notNullValue())))
            .andReturn();

        String responseContent = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        DataResponse response = objectMapper.readValue(responseContent, DataResponse.class);
        LinkedHashMap authResponseDto = (LinkedHashMap) response.getData();
        this.accessToken = authResponseDto.get("accessToken").toString();
        this.refreshToken = authResponseDto.get("refreshToken").toString();
    }

    @Test
    @Order(4)
    void getProfile() throws Exception {
        System.out.println(this.accessToken);
        ResultActions resultActions = mockMvc.perform(get("/api/users/profiles")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", this.accessToken)
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print());

        resultActions
            .andExpectAll(status().isOk(),
                jsonPath("data.name").value(testName),
                jsonPath("data.email").value(testEmail),
                jsonPath("data.role").value(testUserRole.toString()));
    }

    @Test
    @Order(5)
    void updateProfile() throws Exception {
        ProfileRequestDto requestDto = createTestProfileRequestDto();
        ResultActions resultActions = mockMvc.perform(patch("/api/users/profiles")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", this.accessToken)
                .content(objectMapper.writeValueAsString(requestDto))
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print());

        resultActions
            .andExpectAll(status().isOk(),
                jsonPath("data.name").value(requestDto.getName()),
                jsonPath("data.intro").value(requestDto.getIntro()));
    }

    @Test
    @Order(6)
    void updatePassword() throws Exception {
        PasswordRequestDto requestDto = createTestPasswordRequestDto();
        ResultActions resultActions = mockMvc.perform(patch("/api/users/profiles/passwords")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", this.accessToken)
                .content(objectMapper.writeValueAsString(requestDto))
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print());

        resultActions
            .andExpect(status().isOk());
    }
    @Test
    @Order(7)
    void getPoints() throws Exception {
        ResultActions resultActions = mockMvc.perform(get("/api/users/points")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", this.accessToken)
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print());
        Users user = usersRepository.findByEmail("test@email.com").orElse(new Users());

        resultActions
            .andExpectAll(status().isOk(),
                jsonPath("data.point").value(user.getPoint()));
    }

    @Test
    @Order(8)
    void addPoints() throws Exception{
        PointRequestDto requestDto = createTestAddPointRequestDto();
        ResultActions resultActions = mockMvc.perform(patch("/api/users/points")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", this.accessToken)
                .content(objectMapper.writeValueAsString(requestDto))
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print());
        Users user = usersRepository.findByEmail("test@email.com").orElse(new Users());

        resultActions
            .andExpectAll(status().isOk(),
                jsonPath("data.point").value(user.getPoint()));
    }
    @Test
    @Order(9)
    void reissueToken() throws Exception{
        Thread.sleep(1000);
        ReissueTokenRequestDto requestDto = createTestReissueTokenRequestDto();
        ResultActions resultActions = mockMvc.perform(post("/api/users/reissue-token")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", this.accessToken)
                .content(objectMapper.writeValueAsString(requestDto))
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print());

        MvcResult result = resultActions
            .andExpectAll(status().isOk(),
                jsonPath("data.accessToken", is(notNullValue())),
                jsonPath("data.refreshToken", is(notNullValue())))
            .andReturn();

        String responseContent = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        DataResponse response = objectMapper.readValue(responseContent, DataResponse.class);
        LinkedHashMap authResponseDto = (LinkedHashMap) response.getData();
        this.accessToken = authResponseDto.get("accessToken").toString();
        this.refreshToken = authResponseDto.get("refreshToken").toString();
    }

    @Test
    @Order(10)
    void logout() throws Exception {
        Thread.sleep(1000);
        ResultActions resultActions = mockMvc.perform(post("/api/users/logout")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", this.accessToken)
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print());

        resultActions
            .andExpect(status().isOk());
    }

    @Test
    @Order(11)
    void reGetProfile() throws Exception{
        AuthRequestDto requestDto = createTestAuthRequestDto();
        ResultActions resultActions = mockMvc.perform(get("/api/users/profiles")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", this.accessToken)
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print());

        resultActions
            .andExpect(status().isForbidden());
    }
    @Test
    @Order(13)
    void reLogin() throws Exception{
        Thread.sleep(1000);
        AuthRequestDto requestDto = createTestReAuthRequestDto();
        ResultActions resultActions = mockMvc.perform(post("/api/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto))
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print());

        MvcResult result = resultActions
            .andExpectAll(status().isOk(),
                jsonPath("data.accessToken", is(notNullValue())),
                jsonPath("data.refreshToken", is(notNullValue())))
            .andReturn();

        String responseContent = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        DataResponse response = objectMapper.readValue(responseContent, DataResponse.class);
        LinkedHashMap authResponseDto = (LinkedHashMap) response.getData();
        this.accessToken = authResponseDto.get("accessToken").toString();
        this.refreshToken = authResponseDto.get("refreshToken").toString();
    }
    @Test
    @Order(14)
    void withdraw() throws Exception {
        AuthRequestDto requestDto = createTestReAuthRequestDto();
        ResultActions resultActions = mockMvc.perform(post("/api/users/withdraw")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", this.accessToken)
                .content(objectMapper.writeValueAsString(requestDto))
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print());

        resultActions
            .andExpect(status().isOk());
    }
    @Test
    @Order(15)
    void reLoginExpectForbidden() throws Exception{
        Thread.sleep(1000);
        AuthRequestDto requestDto = createTestReAuthRequestDto();
        ResultActions resultActions = mockMvc.perform(post("/api/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto))
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print());

        resultActions
            .andExpect(status().isForbidden());
    }
}