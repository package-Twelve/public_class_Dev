package com.sparta.publicclassdev.domain.winners.repository;

import com.sparta.publicclassdev.domain.teams.entity.Teams;
import com.sparta.publicclassdev.domain.winners.entity.Winners;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WinnersRepository extends JpaRepository<Winners, Long> {
    
    List<Winners> findByTeams(Teams teams);
    
    void deleteAllByTeams(Teams teams);
}
