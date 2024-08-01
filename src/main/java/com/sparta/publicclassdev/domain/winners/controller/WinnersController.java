package com.sparta.publicclassdev.domain.winners.controller;

import com.sparta.publicclassdev.domain.winners.dto.WinnersResponseDto;
import com.sparta.publicclassdev.domain.winners.service.WinnersService;
import com.sparta.publicclassdev.global.dto.DataResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/winners")
public class WinnersController {
    
    private final WinnersService winnersService;
    
    @GetMapping
    public ResponseEntity<DataResponse<List<WinnersResponseDto>>> getAllWinners() {
        List<WinnersResponseDto> response = winnersService.findAllWinners();
        return ResponseEntity.status(HttpStatus.OK)
            .body(new DataResponse<>(200, "우승자 목록 조회 성공", response));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<DataResponse<WinnersResponseDto>> getWinnerById(@PathVariable Long id) {
        WinnersResponseDto response = winnersService.findWinnerById(id);
        return ResponseEntity.status(HttpStatus.OK)
            .body(new DataResponse<>(200, "우승자 조회 성공", response));
    }
}
