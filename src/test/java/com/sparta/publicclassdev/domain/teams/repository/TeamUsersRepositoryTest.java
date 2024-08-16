package com.sparta.publicclassdev.domain.teams.repository;

import static org.junit.jupiter.api.Assertions.*;

import com.sparta.publicclassdev.domain.teams.entity.TeamUsers;
import com.sparta.publicclassdev.domain.teams.entity.Teams;
import com.sparta.publicclassdev.domain.users.entity.RoleEnum;
import com.sparta.publicclassdev.domain.users.entity.Users;
import com.sparta.publicclassdev.domain.users.repository.UsersRepository;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@EnableJpaAuditing
class TeamUsersRepositoryTest {
    
    @Autowired
    private TeamUsersRepository teamUsersRepository;
    
    @Autowired
    private UsersRepository usersRepository;
    
    @Autowired
    private TeamsRepository teamsRepository;
    
    private Users user;
    private Teams team;
    private TeamUsers teamUser;
    
    @BeforeEach
    void setUp() {
        teamUsersRepository.deleteAll();
        usersRepository.deleteAll();
        teamsRepository.deleteAll();
        
        user = Users.builder()
            .name("testuser")
            .email("testuser@email.com")
            .password("password")
            .point(0)
            .role(RoleEnum.USER)
            .build();
        user = usersRepository.save(user);
        
        team = Teams.builder()
            .name("testteam")
            .build();
        team = teamsRepository.save(team);
        
        teamUser = TeamUsers.builder()
            .users(user)
            .teams(team)
            .build();
        teamUser = teamUsersRepository.save(teamUser);
    }
    
    @Test
    @DisplayName("User Entity로 TeamUser 확인")
    void existsByUsers() {
        boolean exists = teamUsersRepository.existsByUsers(user);
        assertTrue(exists);
    }
    
    @Test
    @DisplayName("User Entity로 TeamUser 목록 조회")
    void findByUsers() {
        List<TeamUsers> teamUsersList = teamUsersRepository.findByUsers(user);
        assertEquals(1, teamUsersList.size());
        assertEquals(teamUser, teamUsersList.get(0));
    }
    
    @Test
    @DisplayName("Team Entity로 TeamUser 삭제")
    void deleteAllByTeams() {
        teamUsersRepository.deleteAllByTeams(team);
        List<TeamUsers> teamUsersList = teamUsersRepository.findByUsers(user);
        assertTrue(teamUsersList.isEmpty());
    }
}