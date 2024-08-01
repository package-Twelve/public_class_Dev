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
import org.springframework.web.bind.annotation.DeleteMapping;
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
    public ResponseEntity<MessageResponse> applyMatch(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        teamsService.addWaitQueue(userDetails.getUser());
        return ResponseEntity.status(HttpStatus.OK)
            .body(new MessageResponse(200, "팀 매칭 성공"));
    }
    
    @PostMapping("/create")
    public ResponseEntity<DataResponse<TeamResponseDto>> createTeam() {
        TeamResponseDto response = teamsService.createTeam();
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(new DataResponse<>(201, "팀 생성 성공", response));
    }
    
    @GetMapping("/myteam")
    public ResponseEntity<DataResponse<TeamResponseDto>> getTeamByCurrentUser(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        TeamResponseDto responseDto = teamsService.getTeamByUserEmail(userDetails.getEmail());
        return ResponseEntity.status(HttpStatus.OK)
            .body(new DataResponse<>(200, "팀 조회 성공", responseDto));
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
