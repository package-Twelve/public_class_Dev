package com.sparta.publicclassdev.domain.codekatas.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.sparta.publicclassdev.TestConfig;
import com.sparta.publicclassdev.domain.codekatas.dto.CodeKatasRequestDto;
import com.sparta.publicclassdev.domain.codekatas.dto.CodeKatasResponseDto;
import com.sparta.publicclassdev.domain.codekatas.service.CodeKatasService;
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.Collections;
import javax.sql.DataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@WebMvcTest(CodeKatasController.class)
@ContextConfiguration(classes = {CodeKatasController.class, TestConfig.class})
class CodeKatasControllerTest {
    
    @MockBean
    private CodeKatasService codeKatasService;
    
    @Autowired
    private MockMvc mockMvc;
    
    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new CodeKatasController(codeKatasService))
            .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
            .build();
    }
    
    @Test
    void createCodeKata() throws Exception {
        CodeKatasResponseDto responseDto = new CodeKatasResponseDto(1L, "Test Kata", "Test contents", LocalDate.now());
        when(codeKatasService.createCodeKata(any(), any(CodeKatasRequestDto.class))).thenReturn(responseDto);
        
        mockMvc.perform(post("/api/codekatas/createcodekata")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\":\"Test Kata\", \"contents\":\"Test contents\"}"))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.statusCode").value(201))
            .andExpect(jsonPath("$.message").value("코드카타 생성 성공"))
            .andExpect(jsonPath("$.data.title").value("Test Kata"))
            .andExpect(jsonPath("$.data.contents").value("Test contents"));
    }
    
    @Test
    void getAllCodeKatas() throws Exception {
        Pageable pageable = PageRequest.of(0, 6);
        CodeKatasResponseDto responseDto = new CodeKatasResponseDto(1L, "Test Kata", "Test contents", LocalDate.now());
        Page<CodeKatasResponseDto> page = new PageImpl<>(Collections.singletonList(responseDto), pageable, 1);
        when(codeKatasService.getAllCodeKatas(any(HttpServletRequest.class), any(Pageable.class))).thenReturn(page);
        
        mockMvc.perform(get("/api/codekatas/all")
                .param("page", "0")
                .param("size", "6"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.statusCode").value(200))
            .andExpect(jsonPath("$.message").value("전체 코드카타 조회 성공"))
            .andExpect(jsonPath("$.data.content[0].title").value("Test Kata"))
            .andExpect(jsonPath("$.data.content[0].contents").value("Test contents"));
    }
    
    @Test
    void getCodeKata() throws Exception {
        CodeKatasResponseDto responseDto = new CodeKatasResponseDto(1L, "Test Kata", "Test contents", LocalDate.now());
        when(codeKatasService.getCodeKata(any(HttpServletRequest.class), anyLong())).thenReturn(responseDto);
        
        mockMvc.perform(get("/api/codekatas/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.statusCode").value(200))
            .andExpect(jsonPath("$.message").value("코드카타 조회 성공"))
            .andExpect(jsonPath("$.data.title").value("Test Kata"))
            .andExpect(jsonPath("$.data.contents").value("Test contents"));
    }
    
    @Test
    void updateCodeKata() throws Exception {
        CodeKatasRequestDto requestDto = new CodeKatasRequestDto("Updated Kata", "Updated contents");
        CodeKatasResponseDto responseDto = new CodeKatasResponseDto(1L, "Updated Kata", "Updated contents", LocalDate.now());
        when(codeKatasService.updateCodeKata(any(HttpServletRequest.class), anyLong(), any(CodeKatasRequestDto.class))).thenReturn(responseDto);
        
        mockMvc.perform(put("/api/codekatas/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\":\"Updated Kata\", \"contents\":\"Updated contents\"}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.statusCode").value(200))
            .andExpect(jsonPath("$.message").value("코드카타 수정 성공"))
            .andExpect(jsonPath("$.data.title").value("Updated Kata"))
            .andExpect(jsonPath("$.data.contents").value("Updated contents"));
    }
    
    @Test
    void deleteCodeKata() throws Exception {
        mockMvc.perform(delete("/api/codekatas/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.statusCode").value(200))
            .andExpect(jsonPath("$.message").value("코드카타 삭제 성공"));
    }
    
    @Test
    void createRandomCodeKata() throws Exception {
        CodeKatasResponseDto responseDto = new CodeKatasResponseDto(1L, "Random Kata", "Random contents", LocalDate.now());
        when(codeKatasService.createRandomCodeKata()).thenReturn(responseDto);
        
        mockMvc.perform(post("/api/codekatas/create")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.statusCode").value(201))
            .andExpect(jsonPath("$.message").value("랜덤 코드카타 생성 성공"))
            .andExpect(jsonPath("$.data.title").value("Random Kata"))
            .andExpect(jsonPath("$.data.contents").value("Random contents"));
    }
    
    @Test
    void getTodayCodeKata() throws Exception {
        CodeKatasResponseDto responseDto = new CodeKatasResponseDto(1L, "Today Kata", "Today contents", LocalDate.now());
        when(codeKatasService.getTodayCodeKata()).thenReturn(responseDto);
        
        mockMvc.perform(get("/api/codekatas/today"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.statusCode").value(200))
            .andExpect(jsonPath("$.message").value("오늘의 코드카타 조회 성공"))
            .andExpect(jsonPath("$.data.title").value("Today Kata"))
            .andExpect(jsonPath("$.data.contents").value("Today contents"));
    }
}
