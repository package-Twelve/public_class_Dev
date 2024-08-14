package com.sparta.publicclassdev.domain.teams.repository;

import com.sparta.publicclassdev.domain.teams.entity.TeamUsers;
import com.sparta.publicclassdev.domain.teams.entity.Teams;
import com.sparta.publicclassdev.domain.users.entity.Users;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamUsersRepository extends JpaRepository<TeamUsers, Long> {
    boolean existsByUsers(Users currentUser);
    
    List<TeamUsers> findByUsers(Users users);
    
    void deleteAllByTeams(Teams teams);
}
