package com.sparta.publicclassdev.domain.codekatas.controller;

import com.sparta.publicclassdev.domain.codekatas.dto.CodeKatasDto;
import com.sparta.publicclassdev.domain.codekatas.service.CodeKatasService;
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
    public ResponseEntity<DataResponse<CodeKatasDto>> createCodeKata(HttpServletRequest request, @RequestBody CodeKatasDto codeKatasDto) {
        CodeKatasDto codeKatas = codeKatasService.createCodeKata(request, codeKatasDto);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(new DataResponse<>(201, "코드카타 생성 성공", codeKatas));
    }
    
    @GetMapping("/all")
    public ResponseEntity<DataResponse<List<CodeKatasDto>>> getAllCodeKata(HttpServletRequest request) {
        List<CodeKatasDto> codeKatas = codeKatasService.getAllCodeKatas(request);
        return ResponseEntity.status(HttpStatus.OK)
            .body(new DataResponse<>(200, "전체 코드카타 조회 성공", codeKatas));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<DataResponse<CodeKatasDto>> getCodeKata(HttpServletRequest request, @PathVariable Long id) {
        CodeKatasDto codeKatas = codeKatasService.getCodeKata(request, id);
        return ResponseEntity.status(HttpStatus.OK)
            .body(new DataResponse<>(200, "코드카타 조회 성공", codeKatas));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<DataResponse<CodeKatasDto>> updateCodeKata(HttpServletRequest request, @PathVariable Long id, @RequestBody CodeKatasDto codeKatasDto) {
        CodeKatasDto codeKatas = codeKatasService.updateCodeKata(request, id, codeKatasDto);
        return ResponseEntity.status(HttpStatus.OK)
            .body(new DataResponse<>(200, "코드카타 수정 성공", codeKatas));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<MessageResponse> deleteCodeKata(HttpServletRequest request, @PathVariable Long id) {
        codeKatasService.deleteCodeKata(request, id);
        return ResponseEntity.status(HttpStatus.OK)
            .body(new MessageResponse(200, "코드카타 삭제 성공"));
    }
    
    @PostMapping("/create")
    public ResponseEntity<DataResponse<CodeKatasDto>> createRandomCodeKata(HttpServletRequest request) {
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