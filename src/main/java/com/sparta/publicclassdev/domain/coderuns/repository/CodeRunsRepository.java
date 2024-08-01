package com.sparta.publicclassdev.domain.coderuns.repository;

import com.sparta.publicclassdev.domain.coderuns.entity.CodeRuns;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CodeRunsRepository extends JpaRepository<CodeRuns, Long> {

    Optional<CodeRuns> findByTeamsIdAndCodeKatasId(Long teamsId, Long codeKatasId);

    List<CodeRuns> findAllByTeamsId(Long teamsId);
    
    List<CodeRuns> findByCreatedAtBetween(LocalDateTime startDay, LocalDateTime endDay);
}
