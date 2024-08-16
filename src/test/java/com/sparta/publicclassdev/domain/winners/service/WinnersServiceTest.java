package com.sparta.publicclassdev.domain.winners.service;

import com.sparta.publicclassdev.domain.codekatas.entity.CodeKatas;
import com.sparta.publicclassdev.domain.coderuns.entity.CodeRuns;
import com.sparta.publicclassdev.domain.coderuns.repository.CodeRunsRepository;
import com.sparta.publicclassdev.domain.teams.entity.Teams;
import com.sparta.publicclassdev.domain.teams.repository.TeamsRepository;
import com.sparta.publicclassdev.domain.users.entity.RoleEnum;
import com.sparta.publicclassdev.domain.users.entity.Users;
import com.sparta.publicclassdev.domain.winners.dto.WinnersRequestDto;
import com.sparta.publicclassdev.domain.winners.dto.WinnersResponseDto;
import com.sparta.publicclassdev.domain.winners.entity.Winners;
import com.sparta.publicclassdev.domain.winners.repository.WinnersRepository;
import com.sparta.publicclassdev.global.exception.CustomException;
import com.sparta.publicclassdev.global.exception.ErrorCode;
import com.sparta.publicclassdev.global.security.JwtUtil;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import jakarta.servlet.http.HttpServletRequest;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class WinnersServiceTest {
    
    @Mock
    private CodeRunsRepository codeRunsRepository;
    
    @Mock
    private WinnersRepository winnersRepository;
    
    @Mock
    private TeamsRepository teamsRepository;
    
    @Mock
    private JwtUtil jwtUtil;
    
    @InjectMocks
    private WinnersService winnersService;
    
    private CodeRuns mockCodeRun;
    private Teams mockTeam;
    private Winners mockWinner;
    private Users users;
    private HttpServletRequest request;
    private Claims claims;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        mockCodeRun = mock(CodeRuns.class);
        mockTeam = mock(Teams.class);
        mockWinner = mock(Winners.class);
        request = mock(HttpServletRequest.class);
        claims = mock(Claims.class);
        
        users = Users.builder()
            .name("testuser")
            .email("testuser@email.com")
            .password("password")
            .point(100)
            .role(RoleEnum.ADMIN)
            .build();
        
        when(mockCodeRun.getId()).thenReturn(1L);
        when(mockCodeRun.getResponseTime()).thenReturn(100L);
        when(mockCodeRun.getCode()).thenReturn("test code");
        when(mockCodeRun.getLanguage()).thenReturn("Java");
        when(mockCodeRun.getResult()).thenReturn("success");
        when(mockCodeRun.getTeams()).thenReturn(mockTeam);
        when(mockTeam.getName()).thenReturn("testteam");
        
        CodeKatas mockCodeKatas = mock(CodeKatas.class);
        when(mockCodeRun.getCodeKatas()).thenReturn(mockCodeKatas);
        
        when(mockWinner.getId()).thenReturn(1L);
        when(mockWinner.getCode()).thenReturn("test code");
        when(mockWinner.getLanguage()).thenReturn("Java");
        when(mockWinner.getResponseTime()).thenReturn(100L);
        when(mockWinner.getResult()).thenReturn("success");
        when(mockWinner.getTeamName()).thenReturn("testteam");
        when(mockWinner.getDate()).thenReturn(LocalDate.now());
        when(mockWinner.getCodeKatas()).thenReturn(mockCodeKatas);
        
        when(codeRunsRepository.findById(mockCodeRun.getId())).thenReturn(Optional.of(mockCodeRun));
        when(teamsRepository.findById(mockTeam.getId())).thenReturn(Optional.of(mockTeam));
        
        when(jwtUtil.getJwtFromHeader(request)).thenReturn("token");
        when(jwtUtil.getUserInfoFromToken("token")).thenReturn(claims);
        when(claims.get("auth")).thenReturn("ADMIN");
    }
    
    
    @Test
    @DisplayName("모든 우승자 조회 테스트")
    void FindAllWinners() {
        when(winnersRepository.findAll()).thenReturn(List.of(mockWinner));
        
        List<WinnersResponseDto> winnersList = winnersService.findAllWinners();
        
        assertEquals(1, winnersList.size());
        assertEquals("test code", winnersList.get(0).getCode());
    }
    
    @Test
    @DisplayName("ID로 우승자 조회 성공 테스트")
    void FindWinnerById() {
        when(winnersRepository.findById(1L)).thenReturn(Optional.of(mockWinner));
        
        WinnersResponseDto winnerDto = winnersService.findWinnerById(1L);
        
        assertNotNull(winnerDto);
        assertEquals(1L, winnerDto.getId());
    }
    
    @Test
    @DisplayName("ID로 우승자 조회 실패 테스트")
    void FindWinnerById_NotFound() {
        when(winnersRepository.findById(1L)).thenReturn(Optional.empty());
        
        CustomException exception = assertThrows(CustomException.class, () -> {
            winnersService.findWinnerById(1L);
        });
        
        assertEquals(ErrorCode.NOT_FOUND_CODEKATA, exception.getErrorCode());
    }
    
    @Test
    @DisplayName("우승자 생성 테스트")
    void CreateWinner() {
        when(codeRunsRepository.findById(1L)).thenReturn(Optional.of(mockCodeRun));
        when(teamsRepository.findById(1L)).thenReturn(Optional.of(mockTeam));
        
        WinnersRequestDto requestDto = new WinnersRequestDto(
            "test code",
            "Java",
            100L,
            "success",
            "testteam",
            LocalDate.now(),
            1L,
            1L,
            1L
        );
        
        WinnersResponseDto responseDto = winnersService.createWinner(requestDto);
        
        assertNotNull(responseDto);
        assertEquals("test code", responseDto.getCode());
    }
    
    @Test
    @DisplayName("우승자 생성 - 팀 미존재 테스트")
    void CreateWinner_TeamNotFound() {
        when(codeRunsRepository.findById(1L)).thenReturn(Optional.of(mockCodeRun));
        when(teamsRepository.findById(1L)).thenReturn(Optional.empty());
        
        WinnersRequestDto requestDto = new WinnersRequestDto(
            "test code",
            "Java",
            100L,
            "success",
            "testteam",
            LocalDate.now(),
            1L,
            1L,
            1L
        );
        
        CustomException exception = assertThrows(CustomException.class, () -> {
            winnersService.createWinner(requestDto);
        });
        
        assertEquals(ErrorCode.TEAM_NOT_FOUND, exception.getErrorCode());
    }
    
    @Test
    @DisplayName("일일 우승자 생성 테스트")
    void DailyWinners() {
        when(codeRunsRepository.findByCreatedAtBetween(any(), any())).thenReturn(List.of(mockCodeRun));
        
        winnersService.dailyWinners();
        
        verify(winnersRepository, times(1)).save(any(Winners.class));
    }
    
    @Test
    @DisplayName("권한 검사 성공 테스트")
    void CreateTodayWinner_Authorized() {
        when(codeRunsRepository.findByCreatedAtBetween(any(), any())).thenReturn(List.of(mockCodeRun));
        
        WinnersResponseDto responseDto = winnersService.createTodayWinner(request);
        
        assertNotNull(responseDto);
        verify(winnersRepository, times(1)).save(any(Winners.class));
    }
    
    @Test
    @DisplayName("우승자 삭제 테스트")
    void DeleteWinner() {
        when(winnersRepository.findById(1L)).thenReturn(Optional.of(mockWinner));
        
        winnersService.deleteWinner(1L, request);
        
        verify(winnersRepository, times(1)).delete(mockWinner);
    }
}
