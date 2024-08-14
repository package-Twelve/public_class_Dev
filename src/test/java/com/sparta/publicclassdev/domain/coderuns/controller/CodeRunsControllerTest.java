package com.sparta.publicclassdev.domain.coderuns.controller;

import com.sparta.publicclassdev.domain.coderuns.dto.CodeRunsRequestDto;
import com.sparta.publicclassdev.domain.coderuns.dto.CodeRunsResponseDto;
import com.sparta.publicclassdev.domain.coderuns.service.CodeRunsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CodeRunsController.class)
class CodeRunsControllerTest {
    
    @MockBean
    private CodeRunsService codeRunsService;
    
    @Autowired
    private MockMvc mockMvc;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(new CodeRunsController(codeRunsService)).build();
    }
    
    @DisplayName("코드 실행 테스트")
    @Test
    void runCode() throws Exception {
        CodeRunsRequestDto requestDto = CodeRunsRequestDto.builder()
            .language("java")
            .code("public class Main { public static void main(String[] args) { System.out.println(\"Hello, World!\"); } }")
            .build();
        
        CodeRunsResponseDto responseDto = CodeRunsResponseDto.builder()
            .id(1L)
            .codeKatasId(1L)
            .teamsId(1L)
            .usersId(1L)
            .responseTime(100L)
            .result("Execution successful")
            .code(requestDto.getCode())
            .language(requestDto.getLanguage())
            .build();
        
        when(codeRunsService.runCode(anyLong(), anyLong(), any(CodeRunsRequestDto.class))).thenReturn(responseDto);
        
        mockMvc.perform(post("/api/coderuns/myteam/1/1/runs")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"language\":\"java\", \"code\":\"public class Main { public static void main(String[] args) { System.out.println(\\\"Hello, World!\\\"); } }\"}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1L))
            .andExpect(jsonPath("$.result").value("Execution successful"))
            .andExpect(jsonPath("$.language").value("java"));
    }
    
    @DisplayName("팀 코드 실행 기록 조회 테스트")
    @Test
    void getCodeRunsByTeam() throws Exception {
        CodeRunsResponseDto responseDto1 = CodeRunsResponseDto.builder()
            .id(1L)
            .codeKatasId(1L)
            .teamsId(1L)
            .usersId(1L)
            .responseTime(100L)
            .result("Execution successful")
            .code("public class Main { public static void main(String[] args) { System.out.println(\"Hello, World!\"); } }")
            .language("java")
            .build();
        
        CodeRunsResponseDto responseDto2 = CodeRunsResponseDto.builder()
            .id(2L)
            .codeKatasId(2L)
            .teamsId(1L)
            .usersId(2L)
            .responseTime(150L)
            .result("Execution successful")
            .code("console.log(\"Hello, World!\");")
            .language("javascript")
            .build();
        
        List<CodeRunsResponseDto> responseList = Arrays.asList(responseDto1, responseDto2);
        
        when(codeRunsService.getCodeRunsByTeam(anyLong())).thenReturn(responseList);
        
        mockMvc.perform(get("/api/coderuns/myteam/1/runs"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].language").value("java"))
            .andExpect(jsonPath("$[1].language").value("javascript"));
    }
}
