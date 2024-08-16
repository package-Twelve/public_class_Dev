package com.sparta.publicclassdev.domain.winners.controller;

import com.sparta.publicclassdev.domain.teams.dto.TeamResponseDto;
import com.sparta.publicclassdev.domain.teams.entity.Teams;
import com.sparta.publicclassdev.domain.teams.service.TeamsService;
import com.sparta.publicclassdev.domain.users.entity.Users;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(com.sparta.publicclassdev.domain.winners.controller.TeamsAdminController.class)
class TeamsAdminControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private TeamsService teamsService;
    
    private TeamResponseDto team1;
    private TeamResponseDto team2;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(new com.sparta.publicclassdev.domain.winners.controller.TeamsAdminController(teamsService)).build();
        
        Teams teamEntity1 = Teams.builder().name("team1").build();
        Teams teamEntity2 = Teams.builder().name("team2").build();
        
        ReflectionTestUtils.setField(teamEntity1, "id", 1L);
        ReflectionTestUtils.setField(teamEntity2, "id", 2L);
        
        Users user1 = Users.builder().name("user1").build();
        Users user2 = Users.builder().name("user2").build();
        Users user3 = Users.builder().name("user3").build();
        Users user4 = Users.builder().name("user4").build();
        
        team1 = TeamResponseDto.builder()
            .teams(teamEntity1)
            .teamMembers(Arrays.asList(user1, user2))
            .build();
        
        team2 = TeamResponseDto.builder()
            .teams(teamEntity2)
            .teamMembers(Arrays.asList(user3, user4))
            .build();
    }
    
    @DisplayName("전체 팀 조회 테스트")
    @Test
    void getAllTeams() throws Exception {
        List<TeamResponseDto> teams = Arrays.asList(team1, team2);
        when(teamsService.getAllTeams(any())).thenReturn(teams);
        
        mockMvc.perform(get("/api/manage/teams/all")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.statusCode").value(200))
            .andExpect(jsonPath("$.message").value("전체 팀 조회 성공"))
            .andExpect(jsonPath("$.data[0].id").value(1L))
            .andExpect(jsonPath("$.data[0].name").value("team1"))
            .andExpect(jsonPath("$.data[0].teamMembers[0]").value("user1"))
            .andExpect(jsonPath("$.data[0].teamMembers[1]").value("user2"))
            .andExpect(jsonPath("$.data[1].id").value(2L))
            .andExpect(jsonPath("$.data[1].name").value("team2"))
            .andExpect(jsonPath("$.data[1].teamMembers[0]").value("user3"))
            .andExpect(jsonPath("$.data[1].teamMembers[1]").value("user4"));
    }
    
    @DisplayName("팀 ID로 팀 조회 테스트")
    @Test
    void getTeamById() throws Exception {
        when(teamsService.getTeamById(anyLong())).thenReturn(team1);
        
        mockMvc.perform(get("/api/manage/teams/{teamsId}", 1L)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.statusCode").value(200))
            .andExpect(jsonPath("$.message").value("팀 조회 성공"))
            .andExpect(jsonPath("$.data.id").value(1L))
            .andExpect(jsonPath("$.data.name").value("team1"))
            .andExpect(jsonPath("$.data.teamMembers[0]").value("user1"))
            .andExpect(jsonPath("$.data.teamMembers[1]").value("user2"));
    }
    
    @DisplayName("팀 ID로 팀 삭제 테스트")
    @Test
    void deleteTeamById() throws Exception {
        doNothing().when(teamsService).deleteTeamById(anyLong(), any());
        
        mockMvc.perform(delete("/api/manage/teams/{teamsId}", 1L)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.statusCode").value(200))
            .andExpect(jsonPath("$.message").value("팀 삭제 성공"));
    }
    
    @DisplayName("전체 팀 삭제 테스트")
    @Test
    void deleteAllTeams() throws Exception {
        doNothing().when(teamsService).deleteAllTeams();
        
        mockMvc.perform(delete("/api/manage/teams/delete-all")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.statusCode").value(200))
            .andExpect(jsonPath("$.message").value("전체 팀 삭제 성공"));
    }
}
