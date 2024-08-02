package com.sparta.publicclassdev.domain.winners.service;

import com.sparta.publicclassdev.domain.coderuns.entity.CodeRuns;
import com.sparta.publicclassdev.domain.coderuns.repository.CodeRunsRepository;
import com.sparta.publicclassdev.domain.winners.dto.WinnersResponseDto;
import com.sparta.publicclassdev.domain.winners.entity.Winners;
import com.sparta.publicclassdev.domain.winners.repository.WinnersRepository;
import com.sparta.publicclassdev.global.exception.CustomException;
import com.sparta.publicclassdev.global.exception.ErrorCode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class WinnersService {
    
    private final CodeRunsRepository codeRunsRepository;
    private final WinnersRepository winnersRepository;
    
    public List<WinnersResponseDto> findAllWinners() {
        return winnersRepository.findAll().stream()
            .map(winners -> WinnersResponseDto.builder()
                .id(winners.getId())
                .code(winners.getCode())
                .language(winners.getLanguage())
                .responseTime(winners.getResponseTime())
                .result(winners.getResult())
                .teamName(winners.getTeamName())
                .date(winners.getDate())
                .codeKataTitle(winners.getCodeKatas().getTitle())
                .codeKataContents(winners.getCodeKatas().getContents())
                .build()
            )
            .collect(Collectors.toList());
    }
    
    public WinnersResponseDto findWinnerById(Long id) {
        return winnersRepository.findById(id)
            .map(winners -> WinnersResponseDto.builder()
                .id(winners.getId())
                .code(winners.getCode())
                .language(winners.getLanguage())
                .responseTime(winners.getResponseTime())
                .result(winners.getResult())
                .teamName(winners.getTeamName())
                .date(winners.getDate())
                .codeKataTitle(winners.getCodeKatas().getTitle())
                .codeKataContents(winners.getCodeKatas().getContents())
                .build()
            )
            .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_CODEKATA));
    }
    
    @Transactional
    public void dailyWinners() {
        List<CodeRuns> codeRunsList = getYesterdayCodeRuns();
        
        codeRunsList.stream()
            .collect(Collectors.groupingBy(codeRuns -> codeRuns.getTeams().getId()))
            .forEach((teamsId, teamCodeRuns) -> {
                CodeRuns bestRun = teamCodeRuns.stream()
                    .min(Comparator.comparingLong(CodeRuns::getResponseTime))
                    .orElse(null);
                if (bestRun != null) {
                    Winners winners = Winners.builder()
                        .code(bestRun.getCode())
                        .language(bestRun.getLanguage())
                        .responseTime(bestRun.getResponseTime())
                        .result(bestRun.getResult())
                        .teamName(bestRun.getTeams().getName())
                        .date(LocalDate.now())
                        .codeRuns(bestRun)
                        .teams(bestRun.getTeams())
                        .codeKatas(bestRun.getCodeKatas())
                        .build();
                    winnersRepository.save(winners);
                }
            });
    }
    
    private List<CodeRuns> getYesterdayCodeRuns() {
        LocalDateTime startDay = LocalDateTime.of(LocalDate.now().minusDays(1), LocalTime.MIN);
        LocalDateTime endDay = LocalDateTime.of(LocalDate.now().minusDays(1), LocalTime.MAX);
        return codeRunsRepository.findByCreatedAtBetween(startDay, endDay);
    }
    
    private CodeRuns getBestRuns(List<CodeRuns> teamCodeRuns) {
        return teamCodeRuns.stream()
            .min(Comparator.comparingLong(CodeRuns::getResponseTime))
            .orElse(null);
    }
}
