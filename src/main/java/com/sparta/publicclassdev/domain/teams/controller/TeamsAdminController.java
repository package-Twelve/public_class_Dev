package com.sparta.publicclassdev.domain.winners.controller;

import com.sparta.publicclassdev.domain.teams.dto.TeamResponseDto;
import com.sparta.publicclassdev.domain.teams.service.TeamsService;
import com.sparta.publicclassdev.global.dto.DataResponse;
import com.sparta.publicclassdev.global.dto.MessageResponse;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/manage/teams")
public class TeamsAdminController {
    
    private final TeamsService teamsService;
    
    @GetMapping("/all")
    public ResponseEntity<DataResponse<List<TeamResponseDto>>> getAllTeams(HttpServletRequest request) {
        List<TeamResponseDto> response = teamsService.getAllTeams(request);
        return ResponseEntity.status(HttpStatus.OK)
            .body(new DataResponse<>(200, "전체 팀 조회 성공", response));
    }
    
    @GetMapping("/{teamsId}")
    public ResponseEntity<DataResponse<TeamResponseDto>> getTeamById(@PathVariable Long teamsId) {
        TeamResponseDto response = teamsService.getTeamById(teamsId);
        return ResponseEntity.status(HttpStatus.OK)
            .body(new DataResponse<>(200, "팀 조회 성공", response));
    }
    
    @DeleteMapping("/{teamsId}")
    public ResponseEntity<MessageResponse> deleteTeamById(@PathVariable Long teamsId, HttpServletRequest request) {
        teamsService.deleteTeamById(teamsId, request);
        return ResponseEntity.status(HttpStatus.OK)
            .body(new MessageResponse(200, "팀 삭제 성공"));
    }
    
    @DeleteMapping("/delete-all")
    public ResponseEntity<MessageResponse> deleteAllTeams() {
        teamsService.deleteAllTeams();
        return ResponseEntity.status(HttpStatus.OK)
            .body(new MessageResponse(200, "전체 팀 삭제 성공"));
    }
}
