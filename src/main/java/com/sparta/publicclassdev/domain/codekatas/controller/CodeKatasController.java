package com.sparta.publicclassdev.domain.codekatas.controller;

import com.sparta.publicclassdev.domain.codekatas.dto.CodeKatasDto;
import com.sparta.publicclassdev.domain.codekatas.service.CodeKatasService;
import com.sparta.publicclassdev.global.dto.DataResponse;
import com.sparta.publicclassdev.global.dto.MessageResponse;
import com.sparta.publicclassdev.global.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/codekatas")
public class CodeKatasController {
    
    private final CodeKatasService codeKatasService;
    
    @PostMapping
    public ResponseEntity<DataResponse<CodeKatasDto>> createCodeKata(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestBody CodeKatasDto codeKatasDto) {
        if (!userDetails.getAuthorities().contains("ROLE_ADMIN")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        CodeKatasDto codeKatas = codeKatasService.createCodeKata(codeKatasDto);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(new DataResponse<>(201, "코드카타 생성 성공", codeKatas));
    }
    
    @GetMapping
    public ResponseEntity<DataResponse<List<CodeKatasDto>>> getAllCodeKata(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        if (!userDetails.getAuthorities().contains("ROLE_ADMIN")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        List<CodeKatasDto> codeKatas = codeKatasService.getAllCodeKatas();
        return ResponseEntity.status(HttpStatus.OK)
            .body(new DataResponse<>(200, "전체 코드카타 조회 성공", codeKatas));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<DataResponse<CodeKatasDto>> getCodeKata(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable Long id) {
        if (!userDetails.getAuthorities().contains("ROLE_ADMIN")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        CodeKatasDto codeKatas = codeKatasService.getCodeKata(id);
        return ResponseEntity.status(HttpStatus.OK)
            .body(new DataResponse<>(200, "코드카타 조회 성공", codeKatas));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<DataResponse<CodeKatasDto>> updateCodeKata(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable Long id, @RequestBody CodeKatasDto codeKatasDto) {
        if (!userDetails.getAuthorities().contains("ROLE_ADMIN")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        CodeKatasDto codeKatas = codeKatasService.updateCodeKata(id, codeKatasDto);
        return ResponseEntity.status(HttpStatus.OK)
            .body(new DataResponse<>(200, "코드카타 수정 성공", codeKatas));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<MessageResponse> deleteCodeKata(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable Long id) {
        if (!userDetails.getAuthorities().contains("ROLE_ADMIN")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        codeKatasService.deleteCodeKata(id);
        return ResponseEntity.status(HttpStatus.OK)
            .body(new MessageResponse(200, "코드카타 삭제 성공"));
    }
    
    @PostMapping("/create")
    public ResponseEntity<DataResponse<CodeKatasDto>> createRandomCodeKata(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        if (!userDetails.getAuthorities().contains("ROLE_ADMIN")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        CodeKatasDto codeKatas = codeKatasService.createRandomCodeKata();
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(new DataResponse<>(201, "랜덤 코드카타 생성 성공", codeKatas));
    }
    
    @GetMapping("/today")
    public ResponseEntity<DataResponse<CodeKatasDto>> getTodayCodeKata() {
        CodeKatasDto codeKatas = codeKatasService.getTodayCodeKata();
        return ResponseEntity.status(HttpStatus.OK)
            .body(new DataResponse<>(200, "오늘의 코드카타 조회 성공", codeKatas));
    }
}
