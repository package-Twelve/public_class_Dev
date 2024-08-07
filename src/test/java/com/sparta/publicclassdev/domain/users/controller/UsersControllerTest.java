package com.sparta.publicclassdev.domain.users.controller;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.publicclassdev.domain.users.dto.AuthRequestDto;
import com.sparta.publicclassdev.domain.users.dto.SignupRequestDto;
import com.sparta.publicclassdev.domain.users.entity.RoleEnum;
import com.sparta.publicclassdev.domain.users.service.UsersService;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@TestMethodOrder(value = MethodOrderer.OrderAnnotation.class)
class UsersControllerTest {
    @Value("${ADMIN_TOKEN}")
    private String ADMIN_TOKEN;
    private String testName = "testuser";
    private String testName1 = "testuser1";
    private String testEmail = "test@email.com";
    private String testEmail1 = "test1@email.com";
    private String testPassword = "Asdf1234!";
    private RoleEnum testUserRole = RoleEnum.USER;
    private RoleEnum testAdminRole = RoleEnum.ADMIN;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private UsersService usersService;

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

    private AuthRequestDto createTestAuthRequestDto() {
        AuthRequestDto requestDto = new AuthRequestDto();

        ReflectionTestUtils.setField(requestDto, "email", testEmail);
        ReflectionTestUtils.setField(requestDto, "password", testPassword);

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

        resultActions
            .andExpectAll(status().isOk(),
                jsonPath("data.accessToken", is(notNullValue())),
                jsonPath("data.refreshToken", is(notNullValue())));
    }

//    @Test
//    @Order(4)
//    void getProfile() throws Exception {
//        AuthRequestDto requestDto = createTestAuthRequestDto();
//
//        ResultActions resultActions = mockMvc.perform(get("/api/users/profile")
//                .contentType(MediaType.APPLICATION_JSON)
//                .header("Authorization", )
//                .content(objectMapper.writeValueAsString(requestDto))
//                .accept(MediaType.APPLICATION_JSON))
//            .andDo(print());
//
//        resultActions
//            .andExpectAll(status().isOk(),
//                jsonPath("data.accessToken", is(notNullValue())),
//                jsonPath("data.refreshToken", is(notNullValue())));
//    }

    @Test
    void updateProfile() {
    }

    @Test
    void updatePassword() {
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
    void getPoints() {
    }

    @Test
    void updatePoints() {
    }
}