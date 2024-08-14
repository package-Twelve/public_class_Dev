package com.sparta.publicclassdev.domain.teams.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sparta.publicclassdev.domain.chatrooms.entity.ChatRooms;
import com.sparta.publicclassdev.domain.chatrooms.repository.ChatRoomUsersRepository;
import com.sparta.publicclassdev.domain.chatrooms.repository.ChatRoomsRepository;
import com.sparta.publicclassdev.domain.coderuns.repository.CodeRunsRepository;
import com.sparta.publicclassdev.domain.teams.dto.TeamRequestDto;
import com.sparta.publicclassdev.domain.teams.dto.TeamResponseDto;
import com.sparta.publicclassdev.domain.teams.entity.Teams;
import com.sparta.publicclassdev.domain.teams.repository.TeamUsersRepository;
import com.sparta.publicclassdev.domain.teams.repository.TeamsRepository;
import com.sparta.publicclassdev.domain.users.entity.RoleEnum;
import com.sparta.publicclassdev.domain.users.entity.Users;
import com.sparta.publicclassdev.domain.users.repository.UsersRepository;
import com.sparta.publicclassdev.domain.winners.repository.WinnersRepository;
import com.sparta.publicclassdev.global.exception.CustomException;
import com.sparta.publicclassdev.global.exception.ErrorCode;
import com.sparta.publicclassdev.global.security.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

class TeamsServiceUnitTest {
    
    @Mock
    private TeamsRepository teamsRepository;
    
    @Mock
    private TeamUsersRepository teamUsersRepository;
    
    @Mock
    private UsersRepository usersRepository;
    
    @Mock
    private ChatRoomsRepository chatRoomsRepository;
    
    @Mock
    private ChatRoomUsersRepository chatRoomUsersRepository;
    
    @Mock
    private CodeRunsRepository codeRunsRepository;
    
    @Mock
    private EntityManager entityManager;
    
    @Mock
    private Query mockQuery;
    
    @Mock
    private WinnersRepository winnersRepository;
    
    @Mock
    private HttpServletRequest httpServletRequest;
    
    @Mock
    private JwtUtil jwtUtil;
    
    @InjectMocks
    private TeamsService teamsService;
    
    private Users users;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        users = Users.builder()
            .name("testuser")
            .email("testuser@email.com")
            .password("password")
            .point(100)
            .role(RoleEnum.ADMIN)
            .build();
        
        when(jwtUtil.getJwtFromHeader(httpServletRequest)).thenReturn("token");
        Claims claims = mock(Claims.class);
        when(jwtUtil.getUserInfoFromToken("token")).thenReturn(claims);
        when(claims.get("auth")).thenReturn("ADMIN");
        
        when(usersRepository.findByEmail(anyString())).thenReturn(Optional.of(users));
        
        when(entityManager.createNativeQuery(anyString())).thenReturn(mockQuery);
        when(mockQuery.executeUpdate()).thenReturn(1);
    }
    
    @Test
    @DisplayName("팀 생성/매칭 테스트")
    void createAndMatchTeam() {
        when(teamUsersRepository.existsByUsers(users)).thenReturn(false);
        
        TeamRequestDto requestDto = new TeamRequestDto(users.getEmail());
        
        TeamResponseDto responseDto = teamsService.createAndMatchTeam(requestDto);
        
        assertNotNull(responseDto);
        verify(teamsRepository, times(1)).save(any(Teams.class));
        verify(chatRoomsRepository, times(1)).save(any(ChatRooms.class));
    }
    
    @Test
    @DisplayName("이미 팀에 속한 경우 예외 테스트")
    void createAndMatchTeam_UserAlreadyInTeam() {
        when(teamUsersRepository.existsByUsers(users)).thenReturn(true);
        
        TeamRequestDto requestDto = new TeamRequestDto(users.getEmail());
        
        CustomException exception = assertThrows(CustomException.class, () -> {
            teamsService.createAndMatchTeam(requestDto);
        });
        
        assertEquals(ErrorCode.USER_ALREADY_TEAM, exception.getErrorCode());
    }
    
    @Test
    @DisplayName("존재하지 않는 팀을 요청할 경우 예외 테스트")
    void getTeamById_TeamNotFound() {
        when(teamsRepository.findById(anyLong())).thenReturn(Optional.empty());
        
        CustomException exception = assertThrows(CustomException.class, () -> {
            teamsService.getTeamById(1L);
        });
        
        assertEquals(ErrorCode.TEAM_NOT_FOUND, exception.getErrorCode());
    }
    
    @Test
    @DisplayName("팀 단건 조회 테스트")
    void getTeamById() {
        Teams team = Teams.builder().name("testteam").build();
        when(teamsRepository.findById(anyLong())).thenReturn(Optional.of(team));
        
        TeamResponseDto responseDto = teamsService.getTeamById(1L);
        
        assertNotNull(responseDto);
        assertEquals("testteam", responseDto.getName());
    }
    
    @Test
    @DisplayName("모든 팀 조회 테스트")
    void getAllTeams() {
        Teams team = Teams.builder().name("testteam").build();
        when(teamsRepository.findAll()).thenReturn(List.of(team));
        
        List<TeamResponseDto> teams = teamsService.getAllTeams(httpServletRequest);
        
        assertNotNull(teams);
        assertEquals(1, teams.size());
    }
    
    @Test
    @DisplayName("팀 삭제 테스트")
    void deleteTeamById() {
        Teams team = Teams.builder().name("testteam").build();
        ReflectionTestUtils.setField(team, "id", 1L);
        
        when(teamsRepository.findById(anyLong())).thenReturn(Optional.of(team));
        
        teamsService.deleteTeamById(1L, httpServletRequest);
        
        verify(chatRoomsRepository, times(1)).deleteAllByTeamsId(anyLong());
        verify(codeRunsRepository, times(1)).deleteAllByTeams(any(Teams.class));
        verify(teamUsersRepository, times(1)).deleteAllByTeams(any(Teams.class));
        verify(winnersRepository, times(1)).deleteAllByTeams(any(Teams.class));
        verify(teamsRepository, times(1)).delete(any(Teams.class));
    }
    
    @Test
    @DisplayName("존재하지 않는 팀을 삭제할 경우 예외 테스트")
    void deleteTeamById_TeamNotFound() {
        when(teamsRepository.findById(anyLong())).thenReturn(Optional.empty());
        
        CustomException exception = assertThrows(CustomException.class, () -> {
            teamsService.deleteTeamById(1L, httpServletRequest);
        });
        
        assertEquals(ErrorCode.TEAM_NOT_FOUND, exception.getErrorCode());
    }
    
    @Test
    @DisplayName("모든 팀 삭제 테스트")
    void deleteAllTeams() {
        Teams team = Teams.builder().name("testteam").build();
        ReflectionTestUtils.setField(team, "id", 1L);
        
        when(teamsRepository.findAll()).thenReturn(List.of(team));
        
        teamsService.deleteAllTeams();
        
        verify(chatRoomsRepository, times(1)).deleteAllByTeamsId(team.getId());
        verify(codeRunsRepository, times(1)).deleteAllByTeams(team);
        verify(teamUsersRepository, times(1)).deleteAllByTeams(team);
        verify(entityManager, times(7)).createNativeQuery(anyString());
        verify(teamsRepository, times(1)).delete(team);
    }
}
