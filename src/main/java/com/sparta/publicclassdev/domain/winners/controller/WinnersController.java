package com.sparta.publicclassdev.domain.winners.controller;

import com.sparta.publicclassdev.domain.winners.dto.WinnersResponseDto;
import com.sparta.publicclassdev.domain.winners.service.WinnersService;
import com.sparta.publicclassdev.global.dto.DataResponse;
import com.sparta.publicclassdev.global.dto.MessageResponse;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/winners")
public class WinnersController {
    
    private final WinnersService winnersService;
    
    @GetMapping
    public ResponseEntity<DataResponse<List<WinnersResponseDto>>> getAllWinners() {
        List<WinnersResponseDto> response = winnersService.findAllWinners();
        return ResponseEntity.ok(new DataResponse<>(200, "우승자 목록 조회 성공", response));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<DataResponse<WinnersResponseDto>> getWinnerById(@PathVariable Long id) {
        WinnersResponseDto response = winnersService.findWinnerById(id);
        return ResponseEntity.ok(new DataResponse<>(200, "우승자 조회 성공", response));
    }
    
    @PostMapping("/create/today")
    public ResponseEntity<DataResponse<WinnersResponseDto>> createWinnerToday(HttpServletRequest request) {
        WinnersResponseDto response = winnersService.createTodayWinner(request);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(new DataResponse<>(201, "오늘 우승자 생성 성공", response));
    }
    
    @DeleteMapping("/delete/{winnerId}")
    public ResponseEntity<MessageResponse> deleteWinner(@PathVariable Long winnerId, HttpServletRequest request) {
        winnersService.deleteWinner(winnerId, request);
        return ResponseEntity.status(HttpStatus.NO_CONTENT)
            .body(new MessageResponse(204, "우승자 삭제 성공"));
    }
}
