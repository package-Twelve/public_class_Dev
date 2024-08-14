package com.sparta.publicclassdev.domain.coderuns.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.publicclassdev.domain.codekatas.entity.CodeKatas;
import com.sparta.publicclassdev.domain.codekatas.repository.CodeKatasRepository;
import com.sparta.publicclassdev.domain.coderuns.dto.CodeRunsRequestDto;
import com.sparta.publicclassdev.domain.coderuns.entity.CodeRuns;
import com.sparta.publicclassdev.domain.coderuns.repository.CodeRunsRepository;
import com.sparta.publicclassdev.domain.teams.entity.TeamUsers;
import com.sparta.publicclassdev.domain.teams.entity.Teams;
import com.sparta.publicclassdev.domain.teams.repository.TeamsRepository;
import com.sparta.publicclassdev.domain.users.entity.RoleEnum;
import com.sparta.publicclassdev.domain.users.entity.Users;
import com.sparta.publicclassdev.domain.users.repository.UsersRepository;
import com.sparta.publicclassdev.global.security.JwtUtil;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
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
public class CodeRunsServiceTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Autowired
    private UsersRepository usersRepository;
    
    @Autowired
    private TeamsRepository teamsRepository;
    
    @Autowired
    private CodeRunsRepository codeRunsRepository;
    
    @Autowired
    private CodeKatasRepository codeKatasRepository;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    private Users user;
    private Teams team;
    private CodeKatas codeKatas;
    private String token;
    
    @BeforeEach
    void setUp() {
        codeRunsRepository.deleteAll();
        usersRepository.deleteAll();
        teamsRepository.deleteAll();
        codeKatasRepository.deleteAll();
        
        user = createUser();
        team = createTeam();
        codeKatas = createCodeKatas();
        
        usersRepository.save(user);
        teamsRepository.save(team);
        codeKatas = codeKatasRepository.save(codeKatas);
        
        addUserToTeam(user, team);
        
        token = jwtUtil.createAccessToken(user);
    }
    
    private Users createUser() {
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
    
    private Teams createTeam() {
        Teams team = Teams.builder()
            .name("testteam")
            .build();
        
        ReflectionTestUtils.setField(team, "id", 1L);
        return team;
    }
    
    private CodeKatas createCodeKatas() {
        CodeKatas codeKatas = CodeKatas.builder()
            .title("test CodeKata")
            .contents("test contents")
            .build();
        
        ReflectionTestUtils.setField(codeKatas, "id", 1L);
        return codeKatas;
    }
    
    private void addUserToTeam(Users user, Teams team) {
        TeamUsers teamUsers = TeamUsers.builder()
            .users(user)
            .teams(team)
            .build();
        
        user.setTeamUsers(new ArrayList<>());
        team.setTeamUsers(new ArrayList<>());
        
        user.getTeamUsers().add(teamUsers);
        team.getTeamUsers().add(teamUsers);
        
        usersRepository.save(user);
        teamsRepository.save(team);
    }
    
    private CodeRunsRequestDto createCodeRunsRequestDto() {
        return CodeRunsRequestDto.builder()
            .language("java")
            .code("public class Test {}")
            .build();
    }
    
    @DisplayName("코드 실행 기록 생성")
    @Test
    public void testCreateCodeRun() throws Exception {
        CodeRunsRequestDto requestDto = createCodeRunsRequestDto();
        
        MvcResult result = mockMvc.perform(
                post("/api/coderuns/myteam/{teamsId}/1/runs", team.getId())
                    .header(HttpHeaders.AUTHORIZATION, token)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(requestDto)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.language").value("java"))
            .andExpect(jsonPath("$.code").value("public class Test {}"))
            .andReturn();
        
        String responseBody = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        assertEquals(1, codeRunsRepository.count());
    }
    
    @DisplayName("팀 코드 실행 기록 조회")
    @Test
    public void testGetCodeRunsByTeam() throws Exception {
        CodeRuns codeRun = CodeRuns.builder()
            .code("public class Test {}")
            .responseTime(123L)
            .result("Success")
            .language("java")
            .teams(team)
            .users(user)
            .codeKatas(codeKatas)
            .build();
        
        codeRunsRepository.save(codeRun);
        
        mockMvc.perform(get("/api/coderuns/myteam/{teamsId}/runs", team.getId())
                .header(HttpHeaders.AUTHORIZATION, token)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].language").value("java"))
            .andExpect(jsonPath("$[0].code").value("public class Test {}"))
            .andExpect(jsonPath("$[0].result").value("Success"));
        
        assertThat(codeRunsRepository.count()).isEqualTo(1);
    }
}
