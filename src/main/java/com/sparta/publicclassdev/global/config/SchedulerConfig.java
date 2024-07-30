package com.sparta.publicclassdev.global.config;

import com.sparta.publicclassdev.domain.teams.service.TeamsService;
import com.sparta.publicclassdev.domain.winners.service.WinnersService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
@RequiredArgsConstructor
public class SchedulerConfig {

    private final TeamsService teamsService;
    private final WinnersService winnersService;
    
    @Scheduled(cron = "0 0 0 * * ?")
    public void dailyWinners() {
        winnersService.dailyWinners();
    }

    @Scheduled(cron = "0 0 0 * * ?")
    public void deleteTeamsMidnight() {
        teamsService.deleteAllTeams();
    }
}
