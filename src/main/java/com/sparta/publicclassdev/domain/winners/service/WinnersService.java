package com.sparta.publicclassdev.domain.winners.service;

import com.sparta.publicclassdev.domain.coderuns.entity.CodeRuns;
import com.sparta.publicclassdev.domain.coderuns.repository.CodeRunsRepository;
import com.sparta.publicclassdev.domain.teams.entity.Teams;
import com.sparta.publicclassdev.domain.teams.repository.TeamsRepository;
import com.sparta.publicclassdev.domain.users.entity.RoleEnum;
import com.sparta.publicclassdev.domain.winners.dto.WinnersRequestDto;
import com.sparta.publicclassdev.domain.winners.dto.WinnersResponseDto;
import com.sparta.publicclassdev.domain.winners.entity.Winners;
import com.sparta.publicclassdev.domain.winners.repository.WinnersRepository;
import com.sparta.publicclassdev.global.exception.CustomException;
import com.sparta.publicclassdev.global.exception.ErrorCode;
import com.sparta.publicclassdev.global.security.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDate;
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
    private final JwtUtil jwtUtil;
    
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
        
        if (codeRunsList.isEmpty()) {
            return;
        }
        
        long minResponseTime = codeRunsList.stream()
            .min(Comparator.comparingLong(CodeRuns::getResponseTime))
            .map(CodeRuns::getResponseTime)
            .orElse(Long.MAX_VALUE);
        
        List<CodeRuns> bestRuns = codeRunsList.stream()
            .filter(codeRun -> codeRun.getResponseTime() == minResponseTime)
            .collect(Collectors.toList());
        
        for (CodeRuns bestRun : bestRuns) {
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
            .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_CODERUN));
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
        LocalDate yesterday = LocalDate.now().minusDays(1);
        return codeRunsRepository.findByCreatedAtBetween(yesterday.atStartOfDay(),
            yesterday.atTime(LocalTime.MAX));
    }
    
    private List<CodeRuns> getTodayCodeRuns() {
        LocalDate today = LocalDate.now();
        return codeRunsRepository.findByCreatedAtBetween(today.atStartOfDay(),
            today.atTime(LocalTime.MAX));
    }
    
    @Transactional
    public WinnersResponseDto createTodayWinner(HttpServletRequest request) {
        checkAdminRole(request);
        
        List<CodeRuns> todayCodeRuns = getTodayCodeRuns();
        
        if (todayCodeRuns.isEmpty()) {
            throw new CustomException(ErrorCode.NOT_FOUND_CODERUN);
        }
        
        CodeRuns bestRun = todayCodeRuns.stream()
            .min(Comparator.comparingLong(CodeRuns::getResponseTime))
            .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_CODERUN));
        
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
        
        return createWinner(requestDto);
    }
    
    public void deleteWinner(Long winnerId, HttpServletRequest request) {
        checkAdminRole(request);
        Winners winner = winnersRepository.findById(winnerId)
            .orElseThrow(() -> new CustomException(ErrorCode.WINNER_NOT_FOUND));
        
        winnersRepository.delete(winner);
    }
    
    private void checkAdminRole(HttpServletRequest request) {
        String token = jwtUtil.getJwtFromHeader(request);
        Claims claims = jwtUtil.getUserInfoFromToken(token);
        String role = "ROLE_" + claims.get("auth").toString().trim();
        if (!RoleEnum.ADMIN.getAuthority().equals(role)) {
            throw new CustomException(ErrorCode.NOT_UNAUTHORIZED);
        }
    }
}
