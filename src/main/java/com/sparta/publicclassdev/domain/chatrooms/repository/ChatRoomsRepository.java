package com.sparta.publicclassdev.domain.chatrooms.repository;

import com.sparta.publicclassdev.domain.chatrooms.entity.ChatRooms;
import com.sparta.publicclassdev.domain.teams.entity.Teams;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRoomsRepository extends JpaRepository<ChatRooms, Long> {
    
    void deleteAllByTeamsId(Long teamsId);
    
    List<ChatRooms> findByTeams(Teams teams);
}
