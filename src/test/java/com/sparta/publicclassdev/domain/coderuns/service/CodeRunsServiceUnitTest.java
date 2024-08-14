package com.sparta.publicclassdev.domain.coderuns.service;

import com.sparta.publicclassdev.domain.codekatas.entity.CodeKatas;
import com.sparta.publicclassdev.domain.codekatas.repository.CodeKatasRepository;
import com.sparta.publicclassdev.domain.coderuns.dto.CodeRunsRequestDto;
import com.sparta.publicclassdev.domain.coderuns.dto.CodeRunsResponseDto;
import com.sparta.publicclassdev.domain.coderuns.entity.CodeRuns;
import com.sparta.publicclassdev.domain.coderuns.repository.CodeRunsRepository;
import com.sparta.publicclassdev.domain.coderuns.runner.CodeRunner;
import com.sparta.publicclassdev.domain.coderuns.runner.JavaCodeRunner;
import com.sparta.publicclassdev.domain.coderuns.runner.JavaScriptCodeRunner;
import com.sparta.publicclassdev.domain.coderuns.runner.PythonCodeRunner;
import com.sparta.publicclassdev.domain.teams.entity.TeamUsers;
import com.sparta.publicclassdev.domain.teams.entity.Teams;
import com.sparta.publicclassdev.domain.teams.repository.TeamsRepository;
import com.sparta.publicclassdev.domain.users.entity.Users;
import com.sparta.publicclassdev.domain.users.repository.UsersRepository;
import com.sparta.publicclassdev.domain.winners.repository.WinnersRepository;
import com.sparta.publicclassdev.global.exception.CustomException;
import com.sparta.publicclassdev.global.exception.ErrorCode;
import java.util.ArrayList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CodeRunsServiceUnitTest {
    
    @Mock
    private CodeRunsRepository codeRunsRepository;
    
    @Mock
    private TeamsRepository teamsRepository;
    
    @Mock
    private CodeKatasRepository codeKatasRepository;
    
    @Mock
    private UsersRepository usersRepository;
    
    @Mock
    private WinnersRepository winnersRepository;
    
    @InjectMocks
    private CodeRunsService codeRunsService;
    
    @Mock
    private Authentication authentication;
    
    @Mock
    private SecurityContext securityContext;
    
    @Mock
    private UserDetails userDetails;
    
    private Users testUser;
    private Teams testTeam;
    private CodeKatas testCodeKatas;
    
    @BeforeEach
    void setup() {
        testUser = new Users();
        testTeam = new Teams();
        testCodeKatas = new CodeKatas();
        
        List<TeamUsers> teamUsersList = new ArrayList<>();
        TeamUsers teamUsers = TeamUsers.builder()
            .users(testUser)
            .teams(testTeam)
            .build();
        teamUsersList.add(teamUsers);
        
        testUser.setTeamUsers(teamUsersList);
        testTeam.setTeamUsers(teamUsersList);
        
        lenient().when(securityContext.getAuthentication()).thenReturn(authentication);
        lenient().when(authentication.isAuthenticated()).thenReturn(true);
        lenient().when(authentication.getPrincipal()).thenReturn(userDetails);
        lenient().when(userDetails.getUsername()).thenReturn("test@example.com");
        SecurityContextHolder.setContext(securityContext);
        
        lenient().when(usersRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));
        lenient().when(teamsRepository.findById(anyLong())).thenReturn(Optional.of(testTeam));
        lenient().when(codeKatasRepository.findById(anyLong())).thenReturn(Optional.of(testCodeKatas));
    }
    
    @Test
    @DisplayName("성공적으로 코드 실행")
    void testRunCode_Success() {
        CodeRunsRequestDto requestDto = CodeRunsRequestDto.builder()
            .language("java")
            .code("public class Test {}")
            .build();
        
        CodeRunsResponseDto responseDto = codeRunsService.runCode(1L, 1L, requestDto);
        
        assertNotNull(responseDto);
        verify(codeRunsRepository, times(2)).save(any(CodeRuns.class));
    }
    
    @Test
    @DisplayName("사용자가 권한이 없는 경우 예외 발생")
    void testRunCode_UserNotAuthorized() {
        when(authentication.isAuthenticated()).thenReturn(false);
        
        CustomException exception = assertThrows(CustomException.class, () -> {
            codeRunsService.runCode(1L, 1L, new CodeRunsRequestDto());
        });
        
        assertEquals(ErrorCode.NOT_UNAUTHORIZED, exception.getErrorCode());
    }
    
    @Test
    @DisplayName("팀을 찾을 수 없는 경우 예외 발생")
    void testRunCode_TeamNotFound() {
        when(teamsRepository.findById(anyLong())).thenReturn(Optional.empty());
        
        CustomException exception = assertThrows(CustomException.class, () -> {
            codeRunsService.runCode(1L, 1L, new CodeRunsRequestDto());
        });
        
        assertEquals(ErrorCode.TEAM_NOT_FOUND, exception.getErrorCode());
    }
    
    @Test
    @DisplayName("코드카타를 찾을 수 없는 경우 예외 발생")
    void testRunCode_CodeKataNotFound() {
        when(codeKatasRepository.findById(anyLong())).thenReturn(Optional.empty());
        
        CustomException exception = assertThrows(CustomException.class, () -> {
            codeRunsService.runCode(1L, 1L, new CodeRunsRequestDto());
        });
        
        assertEquals(ErrorCode.NOT_FOUND_CODEKATA, exception.getErrorCode());
    }

    @Test
    @DisplayName("코드실행 기록 삭제 성공")
    void testDeleteCodeRun_Success() {
        codeRunsService.deleteCodeRun(1L);
        
        verify(winnersRepository, times(1)).deleteByCodeRunsId(1L);
        verify(codeRunsRepository, times(1)).deleteById(1L);
    }
    
    @Test
    @DisplayName("팀 코드실행 기록 조회 성공")
    void testGetCodeRunsByTeam_Success() {
        CodeKatas mockCodeKatas = mock(CodeKatas.class);
        when(mockCodeKatas.getId()).thenReturn(1L);
        
        Teams mockTeam = mock(Teams.class);
        when(mockTeam.getId()).thenReturn(1L);
        
        Users mockUser = mock(Users.class);
        when(mockUser.getId()).thenReturn(1L);
        
        CodeRuns codeRun = mock(CodeRuns.class);
        when(codeRun.getCodeKatas()).thenReturn(mockCodeKatas);
        when(codeRun.getTeams()).thenReturn(mockTeam);
        when(codeRun.getUsers()).thenReturn(mockUser);
        
        when(codeRunsRepository.findAllByTeamsId(anyLong())).thenReturn(List.of(codeRun));
        
        List<CodeRunsResponseDto> responseDtoList = codeRunsService.getCodeRunsByTeam(1L);
        
        assertNotNull(responseDtoList);
        assertFalse(responseDtoList.isEmpty());
        verify(codeRunsRepository, times(1)).findAllByTeamsId(anyLong());
    }
    
    @Test
    @DisplayName("Java 코드 실행 반환")
    void testGetCodeRunner_Java() {
        CodeRunner codeRunner = codeRunsService.getCodeRunner("java");
        assertTrue(codeRunner instanceof JavaCodeRunner);
    }
    
    @Test
    @DisplayName("Python 코드 실행 반환")
    void testGetCodeRunner_Python() {
        CodeRunner codeRunner = codeRunsService.getCodeRunner("python");
        assertTrue(codeRunner instanceof PythonCodeRunner);
    }
    
    @Test
    @DisplayName("JavaScript 코드 실행 반환")
    void testGetCodeRunner_JavaScript() {
        CodeRunner codeRunner = codeRunsService.getCodeRunner("javascript");
        assertTrue(codeRunner instanceof JavaScriptCodeRunner);
    }
    
    @Test
    @DisplayName("지원되지 않는 언어 요청 시 예외 발생")
    void testGetCodeRunner_UnsupportedLanguage() {
        CustomException exception = assertThrows(CustomException.class, () -> {
            codeRunsService.getCodeRunner("ruby");
        });
        
        assertEquals(ErrorCode.NOT_SUPPORT_LANGUAGE, exception.getErrorCode());
    }
}
