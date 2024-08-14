package com.sparta.publicclassdev.domain.teams.controller;

import com.sparta.publicclassdev.domain.teams.dto.TeamRequestDto;
import com.sparta.publicclassdev.domain.teams.dto.TeamResponseDto;
import com.sparta.publicclassdev.domain.teams.service.TeamsService;
import com.sparta.publicclassdev.global.dto.DataResponse;
import com.sparta.publicclassdev.global.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/teams")
public class TeamsController {
    
    private final TeamsService teamsService;
    private final JwtUtil jwtUtil;
    
    @PostMapping("/create")
    public ResponseEntity<DataResponse<TeamResponseDto>> createAndMatchTeam(@RequestHeader("Authorization") String token) {
        String email = jwtUtil.getUserEmailFromToken(jwtUtil.substringToken(token));
        TeamRequestDto request = new TeamRequestDto(email);
        TeamResponseDto response = teamsService.createAndMatchTeam(request);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(new DataResponse<>(201, "팀 생성 및 매칭 성공", response));
    }
    
    @GetMapping("/myteam")
    public ResponseEntity<DataResponse<TeamResponseDto>> getTeamByCurrentUser(@RequestHeader("Authorization") String token) {
        try {
            String email = jwtUtil.getUserEmailFromToken(jwtUtil.substringToken(token));
            TeamResponseDto response = teamsService.getTeamByUserEmail(email);
            return ResponseEntity.status(HttpStatus.OK)
                .body(new DataResponse<>(200, "팀 조회 성공", response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new DataResponse<>(404, "팀을 찾을 수 없습니다", null));
        }
    }
    
    @GetMapping("/{teamsId}")
    public ResponseEntity<DataResponse<TeamResponseDto>> getTeamById(@PathVariable Long teamsId) {
        TeamResponseDto response = teamsService.getTeamById(teamsId);
        return ResponseEntity.status(HttpStatus.OK)
            .body(new DataResponse<>(200, "팀 조회 성공", response));
    }
}
