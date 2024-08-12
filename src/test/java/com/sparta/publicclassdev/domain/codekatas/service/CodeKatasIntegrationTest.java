package com.sparta.publicclassdev.domain.codekatas.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.publicclassdev.domain.codekatas.dto.CodeKatasRequestDto;
import com.sparta.publicclassdev.domain.codekatas.repository.CodeKatasRepository;
import com.sparta.publicclassdev.domain.users.entity.RoleEnum;
import com.sparta.publicclassdev.domain.users.entity.Users;
import com.sparta.publicclassdev.domain.users.repository.UsersRepository;
import com.sparta.publicclassdev.global.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class CodeKatasIntegrationTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Autowired
    private UsersRepository usersRepository;
    
    @Autowired
    private CodeKatasRepository codeKatasRepository;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    private Users users;
    private String token;
    private MockHttpServletRequest request;
    
    @BeforeEach
    void setUp() {
        users = Users.builder()
            .name("testuser")
            .email("testuser@email.com")
            .password(new BCryptPasswordEncoder().encode("password"))
            .role(RoleEnum.ADMIN)
            .point(0)
            .build();
        
        usersRepository.save(users);
        
        token = jwtUtil.createAccessToken(users);
    }
    
    @Test
    public void createCodeKata_shouldReturnCreatedCodeKata() throws Exception {
        CodeKatasRequestDto requestDto = new CodeKatasRequestDto("testkata", "test contents");
        
        MvcResult result = mockMvc.perform(post("/api/codekatas/createcodekata")
                .header(HttpHeaders.AUTHORIZATION, token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.statusCode").value(201))
            .andExpect(jsonPath("$.message").value("코드카타 생성 성공"))
            .andExpect(jsonPath("$.data.title").value("testkata"))
            .andReturn();
        
        String responseBody = result.getResponse().getContentAsString();
        
        assertThat(responseBody).contains("testkata");
        assertThat(responseBody).contains("코드카타 생성 성공");
        assertThat(codeKatasRepository.count()).isEqualTo(1);
    }
    
    @Test
    void getCodeKata() {
    }
    
    @Test
    void getAllCodeKatas() {
    }
    
    @Test
    void deleteCodeKata() {
    }
    
    @Test
    void updateCodeKata() {
    }
}