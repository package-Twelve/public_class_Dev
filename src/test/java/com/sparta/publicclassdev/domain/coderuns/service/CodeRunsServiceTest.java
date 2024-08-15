package com.sparta.publicclassdev.domain.coderuns.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.sparta.publicclassdev.domain.codekatas.entity.CodeKatas;
import com.sparta.publicclassdev.domain.codekatas.repository.CodeKatasRepository;
import com.sparta.publicclassdev.domain.coderuns.dto.CodeRunsRequestDto;
import com.sparta.publicclassdev.domain.coderuns.dto.CodeRunsResponseDto;
import com.sparta.publicclassdev.domain.coderuns.entity.CodeRuns;
import com.sparta.publicclassdev.domain.coderuns.repository.CodeRunsRepository;
import com.sparta.publicclassdev.domain.teams.entity.TeamUsers;
import com.sparta.publicclassdev.domain.teams.entity.Teams;
import com.sparta.publicclassdev.domain.teams.repository.TeamUsersRepository;
import com.sparta.publicclassdev.domain.teams.repository.TeamsRepository;
import com.sparta.publicclassdev.domain.users.entity.RoleEnum;
import com.sparta.publicclassdev.domain.users.entity.Users;
import com.sparta.publicclassdev.domain.users.repository.UsersRepository;
import com.sparta.publicclassdev.global.exception.CustomException;
import com.sparta.publicclassdev.global.security.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

@SpringBootTest
@ActiveProfiles("test")
public class CodeRunsServiceTest {
    
    @Autowired
    private CodeRunsService codeRunsService;
    
    @Autowired
    private CodeRunsRepository codeRunsRepository;
    
    @Autowired
    private TeamsRepository teamsRepository;
    
    @Autowired
    private CodeKatasRepository codeKatasRepository;
    
    @Autowired
    private UsersRepository usersRepository;
    
    @Autowired
    private TeamUsersRepository teamUsersRepository;
    
    private Users adminUser;
    private Teams team;
    private CodeKatas codeKatas;
    
    @BeforeEach
    void setUp() {
        adminUser = createUser();
        usersRepository.save(adminUser);
        
        team = createTeam();
        teamsRepository.save(team);
        
        codeKatas = createCodeKatas();
        codeKatasRepository.save(codeKatas);
        
        addUserToTeam(adminUser, team);
        
        setUpSecurityContext(adminUser);
    }
    
    private void addUserToTeam(Users user, Teams team) {
        usersRepository.save(user);
        teamsRepository.save(team);
        
        TeamUsers teamUsers = TeamUsers.builder()
            .users(user)
            .teams(team)
            .build();
        
        user.setTeamUsers(new ArrayList<>());
        user.getTeamUsers().add(teamUsers);
        team.setTeamUsers(new ArrayList<>());
        team.getTeamUsers().add(teamUsers);
        
        teamUsersRepository.save(teamUsers);
    }
    
    private Users createUser() {
        Users user = Users.builder()
            .name("testuser")
            .email("testuser" + System.currentTimeMillis() + "@email.com")
            .password(new BCryptPasswordEncoder().encode("password"))
            .role(RoleEnum.ADMIN)
            .point(0)
            .build();
        return user;
    }
    
    private void setUpSecurityContext(Users user) {
        UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
            .username(user.getEmail())
            .password(user.getPassword())
            .authorities(user.getRole().toString())
            .build();
        
        Authentication auth = new UsernamePasswordAuthenticationToken(userDetails,
            user.getPassword(), userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }
    
    private Teams createTeam() {
        return Teams.builder()
            .name("testteam")
            .build();
    }
    
    private CodeKatas createCodeKatas() {
        return CodeKatas.builder()
            .title("test CodeKata")
            .contents("test contents")
            .build();
    }
    
    private CodeRunsRequestDto createCodeRunsRequestDto() {
        return CodeRunsRequestDto.builder()
            .language("java")
            .code("public class Test { public static void main(String[] args) {} }")
            .build();
    }
    
    @DisplayName("코드 실행 기록 생성 테스트")
    @Test
    public void testRunCode() {
        CodeRunsRequestDto requestDto = createCodeRunsRequestDto();
        
        CodeRunsResponseDto responseDto = codeRunsService.runCode(team.getId(), codeKatas.getId(), requestDto);
        
        assertThat(responseDto.getLanguage()).isEqualTo("java");
        assertThat(responseDto.getCode()).isEqualTo("public class Test { public static void main(String[] args) {} }");
        
        CodeRuns savedCodeRun = codeRunsRepository.findAll().get(0);
        String result = savedCodeRun.getResult();
        
        assertThat(result).isNotNull();
        assertThat(result).satisfies(res -> {
            if (!res.equals("Success")) {
                assertThat(res).startsWith("Execution time:");
            }
        });
        assertThat(savedCodeRun.getLanguage()).isEqualTo(responseDto.getLanguage());
        assertThat(savedCodeRun.getCode()).isEqualTo(responseDto.getCode());    }
    
    @DisplayName("팀 코드 실행 기록 조회 테스트")
    @Test
    public void testGetCodeRunsByTeam() {
        CodeRunsRequestDto requestDto = createCodeRunsRequestDto();
        codeRunsService.runCode(team.getId(), codeKatas.getId(), requestDto);
        
        List<CodeRunsResponseDto> codeRunsList = codeRunsService.getCodeRunsByTeam(team.getId());
        
        assertThat(codeRunsList).isNotEmpty();
        assertThat(codeRunsList.get(0).getLanguage()).isEqualTo("java");
        assertThat(codeRunsList.get(0).getCode()).isEqualTo(
            "public class Test { public static void main(String[] args) {} }");
    }
    
    @DisplayName("팀 코드 실행 기록 삭제 테스트")
    @Test
    public void testDeleteCodeRun() {
        CodeRunsRequestDto requestDto = createCodeRunsRequestDto();
        CodeRunsResponseDto responseDto = codeRunsService.runCode(team.getId(), codeKatas.getId(),
            requestDto);
        
        codeRunsService.deleteCodeRun(responseDto.getId());
        
        assertThat(codeRunsRepository.count()).isEqualTo(0);
    }
    
    @DisplayName("유효하지 않은 팀 ID로 코드 실행 시 예외 발생 테스트")
    @Test
    public void testRunCodeWithInvalidTeamId() {
        CodeRunsRequestDto requestDto = createCodeRunsRequestDto();
        
        assertThrows(CustomException.class, () -> {
            codeRunsService.runCode(999L, codeKatas.getId(), requestDto);
        });
    }
}
