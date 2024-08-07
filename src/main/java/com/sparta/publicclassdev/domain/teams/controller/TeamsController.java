package com.sparta.publicclassdev.domain.teams.controller;

import com.sparta.publicclassdev.domain.teams.dto.TeamResponseDto;
import com.sparta.publicclassdev.domain.teams.service.TeamsService;
import com.sparta.publicclassdev.global.dto.DataResponse;
import com.sparta.publicclassdev.global.dto.MessageResponse;
import com.sparta.publicclassdev.global.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/teams")
public class TeamsController {
    
    private final TeamsService teamsService;
    private final JwtUtil jwtUtil;
    
    @PostMapping("/create")
    public ResponseEntity<DataResponse<TeamResponseDto>> createAndMatchTeam(@RequestHeader("Authorization") String token) {
        String email = jwtUtil.getUserEmailFromToken(jwtUtil.substringToken(token));
        TeamResponseDto response = teamsService.createAndMatchTeam(email);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(new DataResponse<>(201, "팀 생성 및 매칭 성공", response));
    }
    
    @GetMapping("/myteam")
    public ResponseEntity<DataResponse<TeamResponseDto>> getTeamByCurrentUser(@RequestHeader("Authorization") String token) {
        try {
            String email = jwtUtil.getUserEmailFromToken(jwtUtil.substringToken(token));
            TeamResponseDto responseDto = teamsService.getTeamByUserEmail(email);
            return ResponseEntity.status(HttpStatus.OK)
                .body(new DataResponse<>(200, "팀 조회 성공", responseDto));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new DataResponse<>(404, "팀을 찾을 수 없습니다", null));
        }
    }
    
    @GetMapping("/{teamsId}")
    public ResponseEntity<DataResponse<TeamResponseDto>> getTeamById(@PathVariable Long teamsId) {
        TeamResponseDto responseDto = teamsService.getTeamById(teamsId);
        return ResponseEntity.status(HttpStatus.OK)
            .body(new DataResponse<>(200, "팀 조회 성공", responseDto));
    }
    
    @DeleteMapping("/delete-all")
    public ResponseEntity<MessageResponse> deleteAllTeams() {
        teamsService.deleteAllTeams();
        return ResponseEntity.status(HttpStatus.OK)
            .body(new MessageResponse(200, "팀 전부 삭제 성공"));
    }
}
