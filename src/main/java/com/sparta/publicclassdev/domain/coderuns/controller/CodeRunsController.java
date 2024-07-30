package com.sparta.publicclassdev.domain.coderuns.controller;

import com.sparta.publicclassdev.domain.coderuns.dto.CodeRunsRequestDto;
import com.sparta.publicclassdev.domain.coderuns.dto.CodeRunsResponseDto;
import com.sparta.publicclassdev.domain.coderuns.entity.CodeRuns;
import com.sparta.publicclassdev.domain.coderuns.service.CodeRunsService;
import com.sparta.publicclassdev.global.dto.DataResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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

    @PostMapping("/{teamsId}/{codeKatasId}/runs")
    public ResponseEntity<DataResponse<CodeRunsResponseDto>> runCode(@PathVariable Long teamsId,
                                                                     @PathVariable Long codeKatasId,
                                                                     @RequestBody CodeRunsRequestDto request) {
        CodeRunsResponseDto response = codeRunsService.runCode(teamsId, codeKatasId, request);
        return ResponseEntity.status(HttpStatus.OK)
            .body(new DataResponse<>(201, "코드 실행 성공", response));
    }

    @GetMapping("/{teamsId}/runs")
    public ResponseEntity<DataResponse<List<CodeRuns>>> getCodeRunsByTeam(@PathVariable Long teamsId) {
        List<CodeRuns> codeRuns = codeRunsService.getCodeRunsByTeam(teamsId);
        return ResponseEntity.status(HttpStatus.OK)
            .body(new DataResponse<>(200, "기록 조회 성공", codeRuns));
    }
}
