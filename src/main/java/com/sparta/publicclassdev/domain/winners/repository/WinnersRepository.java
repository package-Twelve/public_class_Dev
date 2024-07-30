package com.sparta.publicclassdev.domain.winners.repository;

import com.sparta.publicclassdev.domain.winners.entity.Winners;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WinnersRepository extends JpaRepository<Winners, Long> {

}
