package com.sparta.publicclassdev.global.config;

import com.sparta.publicclassdev.domain.teams.service.TeamsService;
import com.sparta.publicclassdev.domain.winners.service.WinnersService;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
@RequiredArgsConstructor
@Slf4j
public class SchedulerConfig {

    private final TeamsService teamsService;
    private final WinnersService winnersService;

    @Scheduled(cron = "0 0 0 * * ?")
    public void dailyWinners() {
        log.info("Executing dailyWinners at {}", LocalDateTime.now());
        winnersService.dailyWinners();
    }

    @Scheduled(cron = "0 0 0 * * ?")
    public void deleteTeamsMidnight() {
        log.info("Executing deleteTeamsMidnight at {}", LocalDateTime.now());
        teamsService.deleteAllTeams();
    }
}
