package com.sparta.publicclassdev.domain.winners.service;

import com.sparta.publicclassdev.domain.coderuns.entity.CodeRuns;
import com.sparta.publicclassdev.domain.coderuns.repository.CodeRunsRepository;
import com.sparta.publicclassdev.domain.teams.entity.Teams;
import com.sparta.publicclassdev.domain.teams.repository.TeamsRepository;
import com.sparta.publicclassdev.domain.winners.dto.WinnersRequestDto;
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
    private final TeamsRepository teamsRepository;
    
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
        
        CodeRuns bestRun = codeRunsList.stream()
            .min(Comparator.comparingLong(CodeRuns::getResponseTime))
            .orElse(null);
        
        if (bestRun != null) {
            WinnersRequestDto requestDto = new WinnersRequestDto(
                bestRun.getCode(),
                bestRun.getLanguage(),
                bestRun.getResponseTime(),
                bestRun.getResult(),
                bestRun.getTeams().getName(),
                LocalDate.now(),
                bestRun.getCodeKatas().getId(),
                bestRun.getId(),
                bestRun.getTeams().getId()
            );
            createWinner(requestDto);
        }
    }
    
    @Transactional
    public WinnersResponseDto createWinner(WinnersRequestDto requestDto) {
        CodeRuns codeRuns = codeRunsRepository.findById(requestDto.getCodeRunsId())
            .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_CODEKATA));
        Teams teams = teamsRepository.findById(requestDto.getTeamsId())
            .orElseThrow(() -> new CustomException(ErrorCode.TEAM_NOT_FOUND));
        
        Winners winners = new Winners(
            requestDto.getCode(),
            requestDto.getLanguage(),
            requestDto.getResponseTime(),
            requestDto.getResult(),
            requestDto.getTeamName(),
            requestDto.getDate(),
            codeRuns,
            teams,
            codeRuns.getCodeKatas()
        );
        
        winnersRepository.save(winners);
        
        return WinnersResponseDto.builder()
            .id(winners.getId())
            .code(winners.getCode())
            .language(winners.getLanguage())
            .responseTime(winners.getResponseTime())
            .result(winners.getResult())
            .teamName(winners.getTeamName())
            .date(winners.getDate())
            .codeKataTitle(winners.getCodeKatas().getTitle())
            .codeKataContents(winners.getCodeKatas().getContents())
            .build();
    }
    
    private List<CodeRuns> getYesterdayCodeRuns() {
        LocalDateTime startDay = LocalDateTime.of(LocalDate.now().minusDays(1), LocalTime.MIN);
        LocalDateTime endDay = LocalDateTime.of(LocalDate.now().minusDays(1), LocalTime.MAX);
        return codeRunsRepository.findByCreatedAtBetween(startDay, endDay);
    }
}
