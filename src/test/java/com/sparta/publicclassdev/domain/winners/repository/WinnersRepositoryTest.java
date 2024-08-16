package com.sparta.publicclassdev.domain.winners.repository;

import static org.junit.jupiter.api.Assertions.*;

import com.sparta.publicclassdev.domain.coderuns.entity.CodeRuns;
import com.sparta.publicclassdev.domain.coderuns.repository.CodeRunsRepository;
import com.sparta.publicclassdev.domain.teams.entity.Teams;
import com.sparta.publicclassdev.domain.teams.repository.TeamsRepository;
import com.sparta.publicclassdev.domain.winners.entity.Winners;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@EnableJpaAuditing
class WinnersRepositoryTest {
    
    @Autowired
    private WinnersRepository winnersRepository;
    
    @Autowired
    private TeamsRepository teamsRepository;
    
    @Autowired
    private CodeRunsRepository codeRunsRepository;
    
    private Teams team;
    private CodeRuns codeRun;
    private Winners winner;
    
    @BeforeEach
    void setUp() {
        winnersRepository.deleteAll();
        codeRunsRepository.deleteAll();
        teamsRepository.deleteAll();
        
        team = Teams.builder()
            .name("testteam")
            .build();
        team = teamsRepository.save(team);
        
        codeRun = CodeRuns.builder()
            .code("testcode")
            .responseTime(100L)
            .result("success")
            .language("Java")
            .teams(team)
            .build();
        codeRun = codeRunsRepository.save(codeRun);
        
        winner = Winners.builder()
            .code("testcode")
            .language("Java")
            .responseTime(100L)
            .result("success")
            .teamName(team.getName())
            .date(LocalDate.now())
            .codeRuns(codeRun)
            .teams(team)
            .build();
        winner = winnersRepository.save(winner);
    }
    
    @Test
    @DisplayName("팀 삭제 시 우승자가 남아있는지 테스트")
    void deleteAllByTeams() {
        teamsRepository.delete(team);
        List<Winners> winnersList = winnersRepository.findAll();

        assertFalse(winnersList.isEmpty());
        assertEquals(team.getName(), winnersList.get(0).getTeamName());
    }
    
    @Test
    @DisplayName("코드 실행 ID로 우승자 삭제 테스트")
    void deleteByCodeRunsId() {
        winnersRepository.deleteByCodeRunsId(codeRun.getId());
        List<Winners> winnersList = winnersRepository.findAll();
        assertTrue(winnersList.isEmpty());
    }
    
    @Test
    @DisplayName("우승자 생성 및 조회 테스트")
    void findById() {
        Winners winners = winnersRepository.findById(winner.getId()).orElse(null);
        assertNotNull(winners);
        assertEquals(winner.getCode(), winners.getCode());
    }
}