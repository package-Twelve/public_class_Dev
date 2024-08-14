package com.sparta.publicclassdev.domain.codekatas.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.sparta.publicclassdev.TestConfig;
import com.sparta.publicclassdev.domain.codekatas.dto.CodeKatasRequestDto;
import com.sparta.publicclassdev.domain.codekatas.entity.CodeKatas;
import com.sparta.publicclassdev.domain.codekatas.repository.CodeKatasRepository;
import com.sparta.publicclassdev.domain.users.entity.RoleEnum;
import com.sparta.publicclassdev.domain.users.entity.Users;
import com.sparta.publicclassdev.domain.users.repository.UsersRepository;
import com.sparta.publicclassdev.global.security.JwtUtil;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class CodeKatasServiceTest {
    
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
    
    private Users user;
    private String token;
    
    @BeforeEach
    void setUp() {
        usersRepository.deleteAll();
        codeKatasRepository.deleteAll();
        
        user = createUser();
        usersRepository.save(user);
        token = jwtUtil.createAccessToken(user);
    }
    
    private Users createUser() {
        codeKatasRepository.deleteAll();
        Users user = Users.builder()
            .name("testuser")
            .email("testuser@email.com")
            .password(new BCryptPasswordEncoder().encode("password"))
            .role(RoleEnum.ADMIN)
            .point(0)
            .build();
        
        ReflectionTestUtils.setField(user, "id", 1L);
        return user;
    }
    
    private CodeKatasRequestDto createCodeKatasRequestDto() {
        CodeKatasRequestDto requestDto = new CodeKatasRequestDto();
        ReflectionTestUtils.setField(requestDto, "title", "testkata");
        ReflectionTestUtils.setField(requestDto, "contents", "test contents");
        return requestDto;
    }
    
    @DisplayName("코드카타 생성")
    @Test
    public void testCreateCodeKata() throws Exception {
        CodeKatasRequestDto requestDto = createCodeKatasRequestDto();
        
        MvcResult result = mockMvc.perform(post("/api/codekatas/createcodekata")
                .header(HttpHeaders.AUTHORIZATION, token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.statusCode").value(201))
            .andExpect(jsonPath("$.message").value("코드카타 생성 성공"))
            .andExpect(jsonPath("$.data.title").value("testkata"))
            .andReturn();
        
        String responseBody = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        String message = JsonPath.parse(responseBody).read("$.message");
        
        assertEquals("코드카타 생성 성공", message);
        assertEquals(1, codeKatasRepository.count());
    }
    
    @DisplayName("코드카타 단건 조회")
    @Test
    public void testGetCodeKata() throws Exception {
        CodeKatas codeKata = CodeKatas.builder()
            .title("testkata")
            .contents("test contents")
            .build();
        codeKatasRepository.save(codeKata);
        
        mockMvc.perform(get("/api/codekatas/" + codeKata.getId())
                .header(HttpHeaders.AUTHORIZATION, token)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.title").value("testkata"))
            .andExpect(jsonPath("$.data.contents").value("test contents"));
    }
    
    @DisplayName("코드카타 전체 조회")
    @Test
    public void testGetAllCodeKatas() throws Exception {
        CodeKatas codeKata1 = CodeKatas.builder()
            .title("kata1")
            .contents("contents1")
            .build();
        CodeKatas codeKata2 = CodeKatas.builder()
            .title("kata2")
            .contents("contents2")
            .build();
        codeKatasRepository.save(codeKata1);
        codeKatasRepository.save(codeKata2);
        
        mockMvc.perform(get("/api/codekatas/all")
                .header(HttpHeaders.AUTHORIZATION, token)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.content.length()").value(2))
            .andExpect(jsonPath("$.data.content[0].title").value("kata1"))
            .andExpect(jsonPath("$.data.content[1].title").value("kata2"));
    }
    
    @DisplayName("코드카타 삭제")
    @Test
    public void testDeleteCodeKata() throws Exception {
        CodeKatas codeKata = CodeKatas.builder()
            .title("testkata")
            .contents("test contents")
            .build();
        codeKatasRepository.save(codeKata);
        
        mockMvc.perform(delete("/api/codekatas/" + codeKata.getId())
                .header(HttpHeaders.AUTHORIZATION, token)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value("코드카타 삭제 성공"));
        
        assertThat(codeKatasRepository.findById(codeKata.getId())).isEmpty();
    }
    
    @DisplayName("코드카타 수정")
    @Test
    public void testUpdateCodeKata() throws Exception {
        CodeKatas codeKata = CodeKatas.builder()
            .title("oldtitle")
            .contents("old contents")
            .build();
        codeKatasRepository.save(codeKata);
        
        CodeKatasRequestDto updateRequestDto = new CodeKatasRequestDto("newtitle", "new contents");
        
        mockMvc.perform(put("/api/codekatas/" + codeKata.getId())
                .header(HttpHeaders.AUTHORIZATION, token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequestDto)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.title").value("newtitle"))
            .andExpect(jsonPath("$.data.contents").value("new contents"));
        
        CodeKatas updatedCodeKata = codeKatasRepository.findById(codeKata.getId()).orElseThrow();
        assertThat(updatedCodeKata.getTitle()).isEqualTo("newtitle");
        assertThat(updatedCodeKata.getContents()).isEqualTo("new contents");
    }
}
