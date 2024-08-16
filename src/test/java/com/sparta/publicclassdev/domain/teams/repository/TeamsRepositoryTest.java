package com.sparta.publicclassdev.domain.teams.repository;

import static org.junit.jupiter.api.Assertions.*;

import com.sparta.publicclassdev.domain.teams.entity.Teams;
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
class TeamsRepositoryTest {
    
    @Autowired
    private TeamsRepository teamsRepository;
    
    private Teams team;
    
    @BeforeEach
    void setUp() {
        teamsRepository.deleteAll();
        
        team = Teams.builder()
            .name("testteam")
            .build();
        teamsRepository.save(team);
    }
    
    @Test
    @DisplayName("팀 이름으로 존재여부 확인")
    void existsByName() {
        boolean exists = teamsRepository.existsByName("testteam");
        assertTrue(exists);
        
        boolean noExists = teamsRepository.existsByName("noteam");
        assertFalse(noExists);
    }
}