package com.sparta.publicclassdev.domain.winners.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.sparta.publicclassdev.domain.codekatas.entity.CodeKatas;
import com.sparta.publicclassdev.domain.codekatas.repository.CodeKatasRepository;
import com.sparta.publicclassdev.domain.coderuns.entity.CodeRuns;
import com.sparta.publicclassdev.domain.coderuns.repository.CodeRunsRepository;
import com.sparta.publicclassdev.domain.teams.entity.Teams;
import com.sparta.publicclassdev.domain.teams.repository.TeamsRepository;
import com.sparta.publicclassdev.domain.users.entity.RoleEnum;
import com.sparta.publicclassdev.domain.users.entity.Users;
import com.sparta.publicclassdev.domain.users.repository.UsersRepository;
import com.sparta.publicclassdev.domain.winners.dto.WinnersRequestDto;
import com.sparta.publicclassdev.domain.winners.dto.WinnersResponseDto;
import com.sparta.publicclassdev.domain.winners.entity.Winners;
import com.sparta.publicclassdev.domain.winners.repository.WinnersRepository;
import com.sparta.publicclassdev.global.entity.Timestamped;
import com.sparta.publicclassdev.global.security.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
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
class WinnersServiceTest {
    
    @Autowired
    private WinnersService winnersService;
    
    @Autowired
    private CodeRunsRepository codeRunsRepository;
    
    @Autowired
    private CodeKatasRepository codeKatasRepository;
    
    @Autowired
    private WinnersRepository winnersRepository;
    
    @Autowired
    private TeamsRepository teamsRepository;
    
    @Autowired
    private UsersRepository usersRepository;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    private CodeKatas codeKatas;
    private CodeRuns codeRuns;
    private Teams teams;
    private Users user;
    private String token;
    private HttpServletRequest request;
    
    @BeforeEach
    void setUp() throws NoSuchFieldException, IllegalAccessException {
        user = Users.builder()
            .name("Test User")
            .email("testuser@example.com")
            .password("password")
            .point(100)
            .role(RoleEnum.ADMIN)
            .build();
        user = usersRepository.save(user);
        
        codeKatas = new CodeKatas(null, "Test Kata", "Test Contents", LocalDate.now());
        codeKatas = codeKatasRepository.save(codeKatas);
        
        teams = new Teams("Test Team");
        teams = teamsRepository.save(teams);
        
        codeRuns = new CodeRuns(
            "public class Test { public static void main(String[] args) {} }",
            100L,
            "Success",
            "Java",
            teams,
            codeKatas,
            user
        );
        codeRuns = codeRunsRepository.save(codeRuns);
        
        Field createdAtField = Timestamped.class.getDeclaredField("createdAt");
        createdAtField.setAccessible(true);
        LocalDateTime yesterday = LocalDateTime.now().minusDays(1).with(LocalTime.MIN);
        createdAtField.set(codeRuns, yesterday);
        
        codeRuns = codeRunsRepository.save(codeRuns);
        codeRunsRepository.flush();
        
        List<CodeRuns> allCodeRuns = codeRunsRepository.findAll();
        for (CodeRuns run : allCodeRuns) {
            System.out.println("Database CodeRuns: " + run.getCode() + ", createdAt: " + run.getCreatedAt());
        }
        
        request = Mockito.mock(HttpServletRequest.class);
        String token = jwtUtil.createAccessToken(user);
        
        Mockito.when(request.getHeader("Authorization")).thenReturn(token);
    }
    
    @Test
    @DisplayName("모든 우승자 조회 테스트")
    void findAllWinners() {
        WinnersRequestDto requestDto = new WinnersRequestDto(
            codeRuns.getCode(),
            codeRuns.getLanguage(),
            codeRuns.getResponseTime(),
            codeRuns.getResult(),
            teams.getName(),
            LocalDate.now(),
            codeKatas.getId(),
            codeRuns.getId(),
            teams.getId()
        );
        
        winnersService.createWinner(requestDto);
        
        List<WinnersResponseDto> winnersList = winnersService.findAllWinners();
        
        assertEquals(1, winnersList.size());
        assertEquals(codeRuns.getCode(), winnersList.get(0).getCode());
    }
    
    @Test
    @DisplayName("ID로 우승자 조회 성공 테스트")
    void findWinnerById() {
        WinnersRequestDto requestDto = new WinnersRequestDto(
            codeRuns.getCode(),
            codeRuns.getLanguage(),
            codeRuns.getResponseTime(),
            codeRuns.getResult(),
            teams.getName(),
            LocalDate.now(),
            codeKatas.getId(),
            codeRuns.getId(),
            teams.getId()
        );
        
        WinnersResponseDto createdWinner = winnersService.createWinner(requestDto);
        WinnersResponseDto winner = winnersService.findWinnerById(createdWinner.getId());
        
        assertNotNull(winner);
        assertEquals(createdWinner.getId(), winner.getId());
    }
    
    @Test
    @DisplayName("일일 우승자 생성 테스트")
    void dailyWinners() {
        WinnersRequestDto requestDto = new WinnersRequestDto(
            codeRuns.getCode(),
            codeRuns.getLanguage(),
            codeRuns.getResponseTime(),
            codeRuns.getResult(),
            teams.getName(),
            LocalDate.now(),
            codeKatas.getId(),
            codeRuns.getId(),
            teams.getId()
        );
        winnersService.createWinner(requestDto);
        
        List<Winners> winnersList = winnersRepository.findAll();
        assertFalse(winnersList.isEmpty());
        assertEquals(1, winnersList.size());
        assertEquals(codeRuns.getCode(), winnersList.get(0).getCode());
    }
    
    @Test
    @DisplayName("우승자 생성 테스트")
    void createWinner() {
        WinnersRequestDto requestDto = new WinnersRequestDto(
            codeRuns.getCode(),
            codeRuns.getLanguage(),
            codeRuns.getResponseTime(),
            codeRuns.getResult(),
            teams.getName(),
            LocalDate.now(),
            codeKatas.getId(),
            codeRuns.getId(),
            teams.getId()
        );
        
        WinnersResponseDto responseDto = winnersService.createWinner(requestDto);
        
        assertNotNull(responseDto);
        assertEquals(codeRuns.getCode(), responseDto.getCode());
    }
    
    @Test
    @DisplayName("오늘의 우승자 생성 테스트")
    void createTodayWinner() {
        WinnersResponseDto winner = winnersService.createTodayWinner(request);
        
        String actualCodeInDatabase = "public class Test { public static void main(String[] args) {} }";
        assertNotNull(winner);
        assertEquals(actualCodeInDatabase, winner.getCode());
    }
    
    @Test
    @DisplayName("우승자 삭제 테스트")
    void deleteWinner() {
        WinnersRequestDto requestDto = new WinnersRequestDto(
            codeRuns.getCode(),
            codeRuns.getLanguage(),
            codeRuns.getResponseTime(),
            codeRuns.getResult(),
            teams.getName(),
            LocalDate.now(),
            codeKatas.getId(),
            codeRuns.getId(),
            teams.getId()
        );
        
        WinnersResponseDto createdWinner = winnersService.createWinner(requestDto);
        winnersService.deleteWinner(createdWinner.getId(), request);
        
        Optional<Winners> deletedWinner = winnersRepository.findById(createdWinner.getId());
        assertTrue(deletedWinner.isEmpty());
    }
}
