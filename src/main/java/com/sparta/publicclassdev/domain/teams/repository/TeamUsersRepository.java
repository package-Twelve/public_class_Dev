package com.sparta.publicclassdev.domain.teams.repository;

import com.sparta.publicclassdev.domain.teams.entity.TeamUsers;
import com.sparta.publicclassdev.domain.teams.entity.Teams;
import com.sparta.publicclassdev.domain.users.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamUsersRepository extends JpaRepository<TeamUsers, Long> {
    boolean existsByTeamsAndUsers(Teams teams, Users users);
    
    boolean existsByUsers(Users currentUser);
}
