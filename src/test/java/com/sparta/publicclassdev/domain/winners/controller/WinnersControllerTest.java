package com.sparta.publicclassdev.domain.winners.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.sparta.publicclassdev.domain.winners.dto.WinnersResponseDto;
import com.sparta.publicclassdev.domain.winners.service.WinnersService;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@WebMvcTest(WinnersController.class)
class WinnersControllerTest {
    
    @MockBean
    private WinnersService winnersService;
    
    @Autowired
    private MockMvc mockMvc;
    
    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new WinnersController(winnersService))
            .build();
    }
    
    @DisplayName("모든 우승자 조회 테스트")
    @Test
    void getAllWinners() throws Exception {
        WinnersResponseDto responseDto = WinnersResponseDto.builder()
            .id(1L)
            .code("Test Code")
            .language("Java")
            .responseTime(100L)
            .result("Success")
            .teamName("Test Team")
            .date(LocalDate.now())
            .codeKataTitle("Test Kata")
            .codeKataContents("Test Contents")
            .build();
        
        List<WinnersResponseDto> responseDtoList = Collections.singletonList(responseDto);
        when(winnersService.findAllWinners()).thenReturn(responseDtoList);
        
        mockMvc.perform(get("/api/winners"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.statusCode").value(200))
            .andExpect(jsonPath("$.message").value("우승자 목록 조회 성공"))
            .andExpect(jsonPath("$.data[0].code").value("Test Code"));
    }
    
    @DisplayName("ID로 우승자 조회 테스트")
    @Test
    void getWinnerById() throws Exception {
        WinnersResponseDto responseDto = WinnersResponseDto.builder()
            .id(1L)
            .code("Test Code")
            .language("Java")
            .responseTime(100L)
            .result("Success")
            .teamName("Test Team")
            .date(LocalDate.now())
            .codeKataTitle("Test Kata")
            .codeKataContents("Test Contents")
            .build();
        
        when(winnersService.findWinnerById(anyLong())).thenReturn(responseDto);
        
        mockMvc.perform(get("/api/winners/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.statusCode").value(200))
            .andExpect(jsonPath("$.message").value("우승자 조회 성공"))
            .andExpect(jsonPath("$.data.code").value("Test Code"));
    }
    
    @DisplayName("오늘의 우승자 생성 테스트")
    @Test
    void createWinnerToday() throws Exception {
        WinnersResponseDto responseDto = WinnersResponseDto.builder()
            .id(1L)
            .code("Test Code")
            .language("Java")
            .responseTime(100L)
            .result("Success")
            .teamName("Test Team")
            .date(LocalDate.now())
            .codeKataTitle("Test Kata")
            .codeKataContents("Test Contents")
            .build();
        
        when(winnersService.createTodayWinner(any())).thenReturn(responseDto);
        
        mockMvc.perform(post("/api/winners/create/today")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.statusCode").value(201))
            .andExpect(jsonPath("$.message").value("오늘 우승자 생성 성공"))
            .andExpect(jsonPath("$.data.code").value("Test Code"));
    }
    
    @DisplayName("우승자 삭제 테스트")
    @Test
    void deleteWinner() throws Exception {
        mockMvc.perform(delete("/api/winners/delete/1"))
            .andExpect(status().isNoContent())
            .andExpect(jsonPath("$.statusCode").value(204))
            .andExpect(jsonPath("$.message").value("우승자 삭제 성공"));
    }
}
