package com.sparta.publicclassdev.domain.coderuns.controller;

import com.sparta.publicclassdev.domain.coderuns.dto.CodeRunsRequestDto;
import com.sparta.publicclassdev.domain.coderuns.dto.CodeRunsResponseDto;
import com.sparta.publicclassdev.domain.coderuns.service.CodeRunsService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/coderuns")
public class CodeRunsController {

    private final CodeRunsService codeRunsService;
    
    @PostMapping("/myteam/{teamsId}/{codeKatasId}/runs")
    public ResponseEntity<CodeRunsResponseDto> runCode(@PathVariable Long teamsId,
                                                       @PathVariable Long codeKatasId,
                                                       @Valid @RequestBody CodeRunsRequestDto request) {
        CodeRunsResponseDto response = codeRunsService.runCode(teamsId, codeKatasId, request);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/myteam/{teamsId}/runs")
    public ResponseEntity<List<CodeRunsResponseDto>> getCodeRunsByTeam(@PathVariable Long teamsId) {
        List<CodeRunsResponseDto> response = codeRunsService.getCodeRunsByTeam(teamsId);
        return ResponseEntity.ok(response);
    }
}
