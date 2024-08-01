package com.sparta.publicclassdev.domain.teams.controller;

import com.sparta.publicclassdev.domain.teams.dto.TeamResponseDto;
import com.sparta.publicclassdev.domain.teams.service.TeamsService;
import com.sparta.publicclassdev.global.dto.DataResponse;
import com.sparta.publicclassdev.global.dto.MessageResponse;
import com.sparta.publicclassdev.global.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/teams")
public class TeamsController {
    
    private final TeamsService teamsService;
    
    @PostMapping("/match")
    public ResponseEntity<MessageResponse> applyMatch(
        @AuthenticationPrincipal UserDetailsImpl userDetails) {
        teamsService.teamMatch(userDetails.getUser());
        return ResponseEntity.status(HttpStatus.OK)
            .body(new MessageResponse(200, "팀 매칭 성공"));
    }
    
    @PostMapping("/create")
    public ResponseEntity<DataResponse<TeamResponseDto>> createTeam(
        @AuthenticationPrincipal UserDetailsImpl userDetails) {
        TeamResponseDto response = teamsService.createTeam(userDetails.getUser());
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(new DataResponse<>(201, "팀 생성 성공", response));
    }
    
    @GetMapping("/{teamsId}")
    public ResponseEntity<DataResponse<TeamResponseDto>> getTeamById(
        @AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable Long teamsId) {
                                 Long usersId = userDetails.getUser().getId();
        TeamResponseDto responseDto = teamsService.getTeamById(teamsId, usersId);
        return ResponseEntity.status(HttpStatus.OK)
            .body(new DataResponse<>(200, "팀 조회 성공", responseDto));
    }
}
