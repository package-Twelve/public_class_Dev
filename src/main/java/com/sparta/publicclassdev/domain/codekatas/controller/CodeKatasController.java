package com.sparta.publicclassdev.domain.codekatas.controller;

import com.sparta.publicclassdev.domain.codekatas.dto.CodeKatasRequestDto;
import com.sparta.publicclassdev.domain.codekatas.dto.CodeKatasResponseDto;
import com.sparta.publicclassdev.domain.codekatas.service.CodeKatasService;
import com.sparta.publicclassdev.global.dto.DataResponse;
import com.sparta.publicclassdev.global.dto.MessageResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/codekatas")
public class CodeKatasController {
    
    private final CodeKatasService codeKatasService;
    
    @PostMapping("/createcodekata")
    public ResponseEntity<DataResponse<CodeKatasResponseDto>> createCodeKata(HttpServletRequest request, @RequestBody CodeKatasRequestDto requestDto) {
        CodeKatasResponseDto response = codeKatasService.createCodeKata(request, requestDto);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(new DataResponse<>(201, "코드카타 생성 성공", response));
    }
    
    @GetMapping("/all")
    public ResponseEntity<DataResponse<Page<CodeKatasResponseDto>>> getAllCodeKata(HttpServletRequest request, @PageableDefault(size = 6) Pageable pageable) {
        Page<CodeKatasResponseDto> codeKatas = codeKatasService.getAllCodeKatas(request, pageable);
        return ResponseEntity.status(HttpStatus.OK)
            .body(new DataResponse<>(200, "전체 코드카타 조회 성공", codeKatas));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<DataResponse<CodeKatasResponseDto>> getCodeKata(HttpServletRequest request, @PathVariable Long id) {
        CodeKatasResponseDto respons = codeKatasService.getCodeKata(request, id);
        return ResponseEntity.status(HttpStatus.OK)
            .body(new DataResponse<>(200, "코드카타 조회 성공", respons));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<DataResponse<CodeKatasResponseDto>> updateCodeKata(HttpServletRequest request, @PathVariable Long id, @RequestBody CodeKatasRequestDto requestDto) {
        CodeKatasResponseDto response = codeKatasService.updateCodeKata(request, id, requestDto);
        return ResponseEntity.status(HttpStatus.OK)
            .body(new DataResponse<>(200, "코드카타 수정 성공", response));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<MessageResponse> deleteCodeKata(HttpServletRequest request, @PathVariable Long id) {
        codeKatasService.deleteCodeKata(request, id);
        return ResponseEntity.status(HttpStatus.OK)
            .body(new MessageResponse(200, "코드카타 삭제 성공"));
    }
    
    @PostMapping("/create")
    public ResponseEntity<DataResponse<CodeKatasResponseDto>> createRandomCodeKata(HttpServletRequest request) {
        CodeKatasResponseDto response = codeKatasService.createRandomCodeKata();
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(new DataResponse<>(201, "랜덤 코드카타 생성 성공", response));
    }
    
    @GetMapping("/today")
    public ResponseEntity<DataResponse<CodeKatasResponseDto>> getTodayCodeKata() {
        CodeKatasResponseDto response = codeKatasService.getTodayCodeKata();
        return ResponseEntity.status(HttpStatus.OK)
            .body(new DataResponse<>(200, "오늘의 코드카타 조회 성공", response));
    }
}