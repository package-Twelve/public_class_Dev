package com.sparta.publicclassdev.domain.coderuns.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sparta.publicclassdev.domain.codekatas.entity.CodeKatas;
import com.sparta.publicclassdev.domain.codekatas.repository.CodeKatasRepository;
import com.sparta.publicclassdev.domain.coderuns.dto.CodeRunsRequestDto;
import com.sparta.publicclassdev.domain.coderuns.dto.CodeRunsResponseDto;
import com.sparta.publicclassdev.domain.coderuns.entity.CodeRuns;
import com.sparta.publicclassdev.domain.coderuns.repository.CodeRunsRepository;
import com.sparta.publicclassdev.domain.coderuns.runner.CodeRunner;
import com.sparta.publicclassdev.domain.coderuns.runner.JavaCodeRunner;
import com.sparta.publicclassdev.domain.teams.entity.Teams;
import com.sparta.publicclassdev.domain.teams.repository.TeamsRepository;
import com.sparta.publicclassdev.domain.users.entity.RoleEnum;
import com.sparta.publicclassdev.domain.users.entity.Users;
import com.sparta.publicclassdev.domain.users.repository.UsersRepository;
import com.sparta.publicclassdev.domain.winners.repository.WinnersRepository;
import com.sparta.publicclassdev.global.security.UserDetailsImpl;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

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
    
    @Mock
    private Authentication authentication;
    
    @Mock
    private UserDetailsImpl userDetails;
    
    @InjectMocks
    private CodeRunsService codeRunsService;
    
    private Users users;
    private Teams teams;
    private CodeKatas codeKatas;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        
        users = Users.builder()
            .name("test")
            .email("test@email.com")
            .password("password")
            .point(0)
            .role(RoleEnum.USER)
            .build();
        
        teams = Teams.builder()
            .name("test team")
            .build();
        
        codeKatas = CodeKatas.builder()
            .title("test kata")
            .contents("test contents")
            .markDate(LocalDate.now())
            .build();
        
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn("test@email.com");
        when(usersRepository.findByEmail(anyString())).thenReturn(Optional.of(users));
        when(teamsRepository.findById(anyLong())).thenReturn(Optional.of(teams));
        when(codeKatasRepository.findById(anyLong())).thenReturn(Optional.of(codeKatas));
    }
    
    @DisplayName("코드 실행 테스트")
    @Test
    void runCode() {
        CodeRunsRequestDto requestDto = CodeRunsRequestDto.builder()
            .language("Java")
            .code(
                "public class Main { public static void main(String[] args) { System.out.println(\\\"Hello, world!\\\"); } }")
            .build();
        
        when(codeRunsRepository.findByTeamsIdAndCodeKatasId(anyLong(), anyLong())).thenReturn(Optional.empty());
        when(codeRunsRepository.save(any(CodeRuns.class))).thenAnswer(invocation -> invocation.getArgument(0));
        
        CodeRunner codeRunner = mock(JavaCodeRunner.class);
        when(codeRunner.runCode(anyString())).thenReturn("Execution time: 100 ms");
        
        CodeRunsResponseDto responseDto = codeRunsService.runCode(teams.getId(), codeKatas.getId(), requestDto);
        
        assertEquals(teams.getId(), responseDto.getTeamsId());
        assertEquals(codeKatas.getId(), responseDto.getCodeKatasId());
        assertEquals(users.getId(), responseDto.getUsersId());
        assertEquals("Java", responseDto.getLanguage());
        assertEquals(requestDto.getCode(), responseDto.getCode());
        assertEquals(100L, responseDto.getResponseTime());
        assertEquals("Execution time: 100 ms", responseDto.getResult());
    }
    
    @DisplayName("코드 삭제 테스트")
    @Test
    void deleteCodeRun() {
        Long codeRunId = 1L;
        
        codeRunsService.deleteCodeRun(codeRunId);
        
        verify(winnersRepository, times(1)).deleteByCodeRunsId(codeRunId);
        verify(codeRunsRepository, times(1)).deleteById(codeRunId);
    }
    
    @DisplayName("가장 빠른 코드 기록")
    @Test
    void getCodeRunsByTeam() {
        CodeRuns codeRun = CodeRuns.builder()
            .code("sample code")
            .responseTime(100L)
            .result("Execution time: 100 ms")
            .language("java")
            .teams(teams)
            .codeKatas(codeKatas)
            .users(users)
            .build();
        
        when(codeRunsRepository.findAllByTeamsId(anyLong())).thenReturn(Collections.singletonList(codeRun));
        
        List<CodeRunsResponseDto> responseList = codeRunsService.getCodeRunsByTeam(teams.getId());
        
        assertEquals(1, responseList.size());
        CodeRunsResponseDto response = responseList.get(0);
        assertEquals("java", response.getLanguage());
        assertEquals("sample code", response.getCode());
        assertEquals(100L, response.getResponseTime());
        assertEquals("Execution time: 100 ms", response.getResult());
    }
}