package com.sparta.publicclassdev.domain.codereview.controller;

import com.sparta.publicclassdev.domain.codereview.dto.CodeReviewsDetailResponseDto;
import com.sparta.publicclassdev.domain.codereview.dto.CodeReviewsListResponseDto;
import com.sparta.publicclassdev.domain.codereview.dto.CodeReviewsRequestDto;
import com.sparta.publicclassdev.domain.codereview.dto.CodeReviewsResponseDto;
import com.sparta.publicclassdev.domain.codereview.dto.CodeReviewsSearchResponseDto;
import com.sparta.publicclassdev.domain.codereview.service.CodeReviewsService;
import com.sparta.publicclassdev.global.dto.DataResponse;
import com.sparta.publicclassdev.global.dto.MessageResponse;
import com.sparta.publicclassdev.global.security.UserDetailsImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class CodeReviewsController {

  private final CodeReviewsService codeReviewsService;

  @PostMapping("/codereviews")
  public ResponseEntity<DataResponse<CodeReviewsResponseDto>> createCodeReview(
      @Valid @RequestBody CodeReviewsRequestDto codeReviewsRequestDto
      , @AuthenticationPrincipal UserDetailsImpl userDetails) {

    CodeReviewsResponseDto response = codeReviewsService.createCodeReview(codeReviewsRequestDto,
        userDetails.getUser());

    return ResponseEntity.status(HttpStatus.CREATED)
        .body(new DataResponse<>(201, "코드 리뷰 게시글 등록 완료", response));
  }

  @GetMapping("/codereviews")
  public ResponseEntity<DataResponse<CodeReviewsListResponseDto>> getAllCodeReviews(
      @RequestParam(defaultValue = "1") int page) {

    CodeReviewsListResponseDto responseList = codeReviewsService.getAllCodeReviews(page - 1);

    return ResponseEntity.status(HttpStatus.OK)
        .body(new DataResponse<>(200, "코드 리뷰 게시글 조회 완료", responseList));
  }

  @GetMapping("/codereviews/{codeReviewsId}")
  public ResponseEntity<DataResponse<CodeReviewsDetailResponseDto>> getCodeReview(
      @PathVariable(name = "codeReviewsId") Long codeReviewsId) {

    CodeReviewsDetailResponseDto response = codeReviewsService.getCodeReview(codeReviewsId);

    return ResponseEntity.status(HttpStatus.OK)
        .body(new DataResponse<>(200, "코드 리뷰 게시글 조회 완료", response));
  }

  @GetMapping("/codereviews/search")
  public ResponseEntity<DataResponse<CodeReviewsSearchResponseDto>> getCodeReviewsByCategory(
      @RequestParam String category
      , @RequestParam(defaultValue = "1") int page) {

    CodeReviewsSearchResponseDto responseList = codeReviewsService.getCodeReviewsByCategory(category,
        page - 1);

    return ResponseEntity.status(HttpStatus.OK)
        .body(new DataResponse<>(200, "코드 리뷰 게시글 검색 완료", responseList));
  }

  @DeleteMapping("/codereviews/{codeReviewsId}")
  public ResponseEntity<MessageResponse> deleteCodeReview(
      @PathVariable(name = "codeReviewsId") Long codeReviewsId,
      @AuthenticationPrincipal UserDetailsImpl userDetails) {

    codeReviewsService.deleteCodeReview(codeReviewsId, userDetails.getUser());

    return ResponseEntity.status(HttpStatus.OK)
        .body(new MessageResponse(200, "코드 리뷰 게시글 삭제 완료"));
  }

  @PutMapping("/codereviews/{codeReviewsId}")
  public ResponseEntity<DataResponse<CodeReviewsResponseDto>> updateCodeReview(
      @Valid @RequestBody CodeReviewsRequestDto codeReviewsRequestDto,
      @PathVariable(name = "codeReviewsId") Long codeReviewsId,
      @AuthenticationPrincipal UserDetailsImpl userDetails) {

    CodeReviewsResponseDto response = codeReviewsService.updateCodeReview(codeReviewsRequestDto,
        codeReviewsId,
        userDetails.getUser());

    return ResponseEntity.status(HttpStatus.OK)
        .body(new DataResponse<>(200, "코드 리뷰 게시글 수정 완료", response));
  }
}
