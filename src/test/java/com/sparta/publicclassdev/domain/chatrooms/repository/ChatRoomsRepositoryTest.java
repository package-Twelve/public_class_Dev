package com.sparta.publicclassdev.domain.chatrooms.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import com.sparta.publicclassdev.domain.chatrooms.entity.ChatRooms;
import com.sparta.publicclassdev.domain.teams.entity.Teams;
import com.sparta.publicclassdev.domain.teams.repository.TeamsRepository;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class ChatRoomsRepositoryTest {
    
    @Autowired
    private ChatRoomsRepository chatRoomsRepository;
    
    @Autowired
    private TeamsRepository teamsRepository;
    
    private Teams teams;
    
    @BeforeEach
    void setUp() {
        teams = teamsRepository.save(Teams.builder()
            .name("testteam")
            .build());
    }
    
    @Test
    @DisplayName("특정 팀 채팅방 조회 테스트")
    void deleteAllByTeamsId() {
        ChatRooms chatRooms1 = chatRoomsRepository.save(ChatRooms.builder()
            .teams(teams)
            .build());
        
        ChatRooms chatRooms2 = chatRoomsRepository.save(ChatRooms.builder()
            .teams(teams)
            .build());
        
        List<ChatRooms> chatRooms = chatRoomsRepository.findByTeams(teams);
        
        assertThat(chatRooms).hasSize(2);
        assertThat(chatRooms).extracting("teams").containsOnly(teams);
    }
    
    @Test
    @DisplayName("특정 팀 채팅방 삭제 테스트")
    void findByTeams() {
        ChatRooms chatRooms1 = chatRoomsRepository.save(ChatRooms.builder()
            .teams(teams)
            .build());
        
        ChatRooms chatRooms2 = chatRoomsRepository.save(ChatRooms.builder()
            .teams(teams)
            .build());
        
        chatRoomsRepository.deleteAllByTeamsId(teams.getId());
        
        List<ChatRooms> chatRooms = chatRoomsRepository.findByTeams(teams);
        assertThat(chatRooms).isEmpty();
    }
}