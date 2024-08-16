package com.sparta.publicclassdev.domain.teams.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.sparta.publicclassdev.domain.teams.dto.TeamRequestDto;
import com.sparta.publicclassdev.domain.teams.dto.TeamResponseDto;
import com.sparta.publicclassdev.domain.teams.entity.Teams;
import com.sparta.publicclassdev.domain.teams.service.TeamsService;
import com.sparta.publicclassdev.domain.users.entity.Users;
import com.sparta.publicclassdev.global.security.JwtUtil;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@WebMvcTest(TeamsController.class)
class TeamsControllerTest {
    
    @MockBean
    private TeamsService teamsService;
    
    @MockBean
    private JwtUtil jwtUtil;
    
    @Autowired
    private MockMvc mockMvc;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(new TeamsController(teamsService, jwtUtil))
            .build();
    }
    
    @DisplayName("팀 생성 및 매칭 테스트")
    @Test
    void createAndMatchTeam() throws Exception {
        Teams team = Teams.builder().name("testteam").build();
        Users user1 = Users.builder().name("user1").build();
        Users user2 = Users.builder().name("user2").build();
        List<Users> teamMembers = Arrays.asList(user1, user2);
        
        TeamResponseDto responseDto = TeamResponseDto.builder()
            .teams(team)
            .teamMembers(teamMembers)
            .build();
        
        when(jwtUtil.getUserEmailFromToken(anyString())).thenReturn("testuser@email.com");
        when(teamsService.createAndMatchTeam(any(TeamRequestDto.class))).thenReturn(responseDto);
        
        mockMvc.perform(post("/api/teams/create")
                .header("Authorization", "Bearer test-token"))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.statusCode").value(201))
            .andExpect(jsonPath("$.message").value("팀 생성 및 매칭 성공"))
            .andExpect(jsonPath("$.data.name").value("testteam"))
            .andExpect(jsonPath("$.data.teamMembers[0]").value("user1"))
            .andExpect(jsonPath("$.data.teamMembers[1]").value("user2"));
    }
    
    @DisplayName("현재 유저의 팀 조회 테스트")
    @Test
    void getTeamByCurrentUser() throws Exception {
        Teams team = Teams.builder().name("testteam").build();
        Users user1 = Users.builder().name("user1").build();
        Users user2 = Users.builder().name("user2").build();
        List<Users> teamMembers = Arrays.asList(user1, user2);
        
        TeamResponseDto responseDto = TeamResponseDto.builder()
            .teams(team)
            .teamMembers(teamMembers)
            .build();
        
        when(jwtUtil.getUserEmailFromToken(anyString())).thenReturn("testuser@email.com");
        when(teamsService.getTeamByUserEmail(anyString())).thenReturn(responseDto);
        
        mockMvc.perform(get("/api/teams/myteam")
                .header("Authorization", "Bearer test-token"))
            .andExpect(status().isOk())
            .andExpect(header().string("Content-Type", "application/json"));
    }
    
    @DisplayName("팀 ID로 팀 조회 테스트")
    @Test
    void getTeamById() throws Exception {
        Teams team = Teams.builder().name("testteam").build();
        Users user1 = Users.builder().name("user1").build();
        Users user2 = Users.builder().name("user2").build();
        List<Users> teamMembers = Arrays.asList(user1, user2);
        
        TeamResponseDto responseDto = TeamResponseDto.builder()
            .teams(team)
            .teamMembers(teamMembers)
            .build();
        
        when(teamsService.getTeamById(1L)).thenReturn(responseDto);
        
        mockMvc.perform(get("/api/teams/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.statusCode").value(200))
            .andExpect(jsonPath("$.message").value("팀 조회 성공"))
            .andExpect(jsonPath("$.data.name").value("testteam"))
            .andExpect(jsonPath("$.data.teamMembers[0]").value("user1"))
            .andExpect(jsonPath("$.data.teamMembers[1]").value("user2"));
    }
}
