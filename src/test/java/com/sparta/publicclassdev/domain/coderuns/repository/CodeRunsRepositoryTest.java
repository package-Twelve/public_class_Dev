package com.sparta.publicclassdev.domain.coderuns.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.sparta.publicclassdev.domain.codekatas.entity.CodeKatas;
import com.sparta.publicclassdev.domain.codekatas.repository.CodeKatasRepository;
import com.sparta.publicclassdev.domain.coderuns.entity.CodeRuns;
import com.sparta.publicclassdev.domain.teams.entity.Teams;
import com.sparta.publicclassdev.domain.teams.repository.TeamsRepository;
import com.sparta.publicclassdev.domain.users.entity.RoleEnum;
import com.sparta.publicclassdev.domain.users.entity.Users;
import com.sparta.publicclassdev.domain.users.repository.UsersRepository;
import java.time.LocalDateTime;
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
class CodeRunsRepositoryTest {
    
    @Autowired
    private CodeRunsRepository codeRunsRepository;
    
    @Autowired
    private TeamsRepository teamsRepository;
    
    @Autowired
    private CodeKatasRepository codeKatasRepository;
    
    @Autowired
    private UsersRepository usersRepository;
    
    private Teams team;
    private CodeKatas codeKatas;
    private Users user;
    private CodeRuns codeRun1;
    private CodeRuns codeRun2;
    
    @BeforeEach
    void setUp() {
        codeRunsRepository.deleteAll();
        teamsRepository.deleteAll();
        codeKatasRepository.deleteAll();
        usersRepository.deleteAll();
        
        LocalDateTime now = LocalDateTime.now();
        
        team = Teams.builder().name("testteam").build();
        team = teamsRepository.save(team);
        
        codeKatas = new CodeKatas();
        codeKatas = codeKatasRepository.save(codeKatas);
        
        user = Users.builder()
            .name("testuser")
            .email("testuser@email.com")
            .password("password")
            .point(0)
            .role(RoleEnum.USER)
            .build();
        user = usersRepository.save(user);
        
        codeRun1 = CodeRuns.builder()
            .code("test code 1")
            .responseTime(100L)
            .result("Success")
            .language("java")
            .teams(team)
            .codeKatas(codeKatas)
            .users(user)
            .build();
        
        codeRun2 = CodeRuns.builder()
            .code("test code 2")
            .responseTime(200L)
            .result("Failure")
            .language("python3")
            .teams(team)
            .codeKatas(codeKatas)
            .users(user)
            .build();
        
        codeRunsRepository.save(codeRun1);
        codeRunsRepository.save(codeRun2);
    }
    
    @DisplayName("팀 ID와 코드 카타 ID로 결과값 찾기")
    @Test
    void findByTeamsIdAndCodeKatasId() {
        List<CodeRuns> foundList = codeRunsRepository.findByTeamsIdAndCodeKatasId(team.getId(), codeKatas.getId());
        assertTrue(!foundList.isEmpty(), "적어도 하나의 결과가 있어야 합니다.");
        CodeRuns found = foundList.get(0);
        assertEquals(codeRun1, found, "찾은 결과과 일치하지 않습니다.");
    }
    
    @DisplayName("팀 ID로 모든 결과값 찾기")
    @Test
    void findAllByTeamsId() {
        List<CodeRuns> codeRunsList = codeRunsRepository.findAllByTeamsId(team.getId());
        assertEquals(2, codeRunsList.size(), "팀 ID로 찾은 결과 개수가 예상과 다릅니다.");
        assertTrue(codeRunsList.contains(codeRun1), "CodeRun1이 결과 목록에 포함되어야 합니다.");
        assertTrue(codeRunsList.contains(codeRun2), "CodeRun2가 결과 목록에 포함되어야 합니다.");
    }
    
    @DisplayName("생성일 사이에서 결과값 찾기")
    @Test
    void findByCreatedAtBetween() {
        LocalDateTime start = LocalDateTime.now().minusDays(2);
        LocalDateTime end = LocalDateTime.now().plusDays(1);
        
        List<CodeRuns> codeRunsList = codeRunsRepository.findByCreatedAtBetween(start, end);
        assertEquals(2, codeRunsList.size(), "생성일 사이에서 찾은 결과의 개수가 예상과 다릅니다.");
        assertTrue(codeRunsList.contains(codeRun1), "CodeRun1이 생성일 사이의 목록에 포함되어야 합니다.");
        assertTrue(codeRunsList.contains(codeRun2), "CodeRun2가 생성일 사이의 목록에 포함되어야 합니다.");
    }
    
    @DisplayName("팀의 모든 결과 삭제")
    @Test
    void deleteAllByTeams() {
        codeRunsRepository.deleteAllByTeams(team);
        
        List<CodeRuns> codeRunsList = codeRunsRepository.findAllByTeamsId(team.getId());
        assertTrue(codeRunsList.isEmpty(), "팀의 모든 결과가 삭제되어야 합니다.");
    }
}
