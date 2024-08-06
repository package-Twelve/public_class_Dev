package com.sparta.publicclassdev.domain.winners.controller;

import com.sparta.publicclassdev.domain.teams.dto.TeamResponseDto;
import com.sparta.publicclassdev.domain.teams.service.TeamsService;
import com.sparta.publicclassdev.global.dto.DataResponse;
import com.sparta.publicclassdev.global.dto.MessageResponse;
import com.sparta.publicclassdev.global.security.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/teams/manage")
public class TeamsAdminController {
    
    private final TeamsService teamsService;
    
    @GetMapping("/all")
    public ResponseEntity<DataResponse<List<TeamResponseDto>>> getAllTeams(HttpServletRequest request) {
        teamsService.checkAdminRole(request);
        List<TeamResponseDto> response = teamsService.getAllTeams();
        return ResponseEntity.status(HttpStatus.OK)
            .body(new DataResponse<>(200, "전체 팀 조회 성공", response));
    }
    
    @DeleteMapping("/delete-all")
    public ResponseEntity<MessageResponse> deleteAllTeams(HttpServletRequest request) {
        teamsService.checkAdminRole(request);
        teamsService.deleteAllTeams();
        return ResponseEntity.status(HttpStatus.OK)
            .body(new MessageResponse(200, "팀 전부 삭제 성공"));
    }
}
