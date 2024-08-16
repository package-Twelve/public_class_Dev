package com.sparta.publicclassdev.domain.teams.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.sparta.publicclassdev.domain.teams.dto.TeamRequestDto;
import com.sparta.publicclassdev.domain.teams.dto.TeamResponseDto;
import com.sparta.publicclassdev.domain.teams.repository.TeamUsersRepository;
import com.sparta.publicclassdev.domain.users.entity.RoleEnum;
import com.sparta.publicclassdev.domain.users.entity.Users;
import com.sparta.publicclassdev.domain.users.repository.UsersRepository;
import com.sparta.publicclassdev.global.exception.CustomException;
import com.sparta.publicclassdev.global.exception.ErrorCode;
import com.sparta.publicclassdev.global.security.JwtUtil;
import jakarta.persistence.EntityManager;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class TeamsServiceTest {
    
    @Autowired
    private TeamsService teamsService;
    
    @Autowired
    private UsersRepository usersRepository;
    
    @Autowired
    private TeamUsersRepository teamUsersRepository;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private EntityManager entityManager;
    
    private Users users;
    private HttpServletRequest mockRequest;
    
    @BeforeEach
    void setUp() {
        users = Users.builder()
            .name("testuser")
            .email("testuser@email.com")
            .password("password")
            .role(RoleEnum.ADMIN)
            .point(0)
            .build();
        users = usersRepository.save(users);
        
        mockRequest = Mockito.mock(HttpServletRequest.class);
        String token = jwtUtil.createAccessToken(users);
        
        Mockito.when(mockRequest.getHeader("Authorization")).thenReturn(token);
    }
    
    @Test
    @DisplayName("팀 생성/매칭 테스트")
    void testCreateAndMatchTeam() {
        entityManager.flush();
        entityManager.clear();
        
        TeamRequestDto requestDto = new TeamRequestDto(users.getEmail());
        TeamResponseDto responseDto = teamsService.createAndMatchTeam(requestDto);
        
        assertNotNull(responseDto);
        assertThat(responseDto.getTeamMembers()).contains(users.getName());
        assertThat(responseDto.getName()).isNotEmpty();
    }
    
    @Test
    @DisplayName("사용자 이메일로 팀 조회 테스트")
    void testGetTeamByUserEmail() {
        TeamRequestDto requestDto = new TeamRequestDto(users.getEmail());
        TeamResponseDto response = teamsService.createAndMatchTeam(requestDto);
        
        entityManager.flush();
        entityManager.clear();
        
        assertThat(response.getTeamMembers()).contains(users.getName());
        
        TeamResponseDto responseDto = teamsService.getTeamByUserEmail(users.getEmail());
        
        assertNotNull(responseDto);
        assertThat(responseDto.getName()).isNotEmpty();
        assertThat(responseDto.getTeamMembers()).contains(users.getName());
    }
    
    
    @Test
    @DisplayName("존재하지 않는 팀 조회 시 예외 테스트")
    void testGetTeamById_NotFound() {
        Long invalidTeamId = 999L;
        
        assertThatThrownBy(() -> teamsService.getTeamById(invalidTeamId))
            .isInstanceOf(CustomException.class)
            .hasFieldOrPropertyWithValue("errorCode", ErrorCode.TEAM_NOT_FOUND);
    }
    
    @Test
    @DisplayName("팀 삭제 테스트")
    void testDeleteTeamById() {
        TeamRequestDto requestDto = new TeamRequestDto(users.getEmail());
        TeamResponseDto responseDto = teamsService.createAndMatchTeam(requestDto);
        
        teamsService.deleteTeamById(responseDto.getId(), mockRequest);
        
        assertThatThrownBy(() -> teamsService.getTeamById(responseDto.getId()))
            .isInstanceOf(CustomException.class);
    }
}
