package com.sparta.publicclassdev.domain.teams.repository;

import com.sparta.publicclassdev.domain.teams.entity.Teams;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamsRepository extends JpaRepository<Teams, Long> {

    boolean existsByName(String teamName);
}
