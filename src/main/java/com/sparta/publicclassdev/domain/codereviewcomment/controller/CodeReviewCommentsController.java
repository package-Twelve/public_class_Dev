package com.sparta.publicclassdev.domain.codereviewcomment.controller;

import com.sparta.publicclassdev.domain.codereviewcomment.dto.CodeReviewCommentsRequestDto;
import com.sparta.publicclassdev.domain.codereviewcomment.dto.CodeReviewCommentsResponseDto;
import com.sparta.publicclassdev.domain.codereviewcomment.service.CodeReviewCommentsService;
import com.sparta.publicclassdev.global.dto.DataResponse;
import com.sparta.publicclassdev.global.dto.MessageResponse;
import com.sparta.publicclassdev.global.security.UserDetailsImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class CodeReviewCommentsController {

  private final CodeReviewCommentsService codeReviewCommentsService;

  @PostMapping("/codereviews/{codeReviewsId}/comments")
  public ResponseEntity<DataResponse<CodeReviewCommentsResponseDto>> createCodeReviewComment(
      @PathVariable(name = "codeReviewsId") Long codeReviewsId,
      @Valid @RequestBody CodeReviewCommentsRequestDto codeReviewCommentsRequestDto,
      @AuthenticationPrincipal UserDetailsImpl userDetails) {

    CodeReviewCommentsResponseDto response = codeReviewCommentsService.createCodeReviewComment(
        codeReviewsId, codeReviewCommentsRequestDto, userDetails.getUser());

    return ResponseEntity.status(HttpStatus.CREATED)
        .body(new DataResponse<>(201, "코드 리뷰 댓글 등록 완료", response));
  }

  @PutMapping("/codereviews/{codeReviewsId}/comments/{codeReviewCommentsId}")
  public ResponseEntity<DataResponse<CodeReviewCommentsResponseDto>> updateCodeReviewComment(
      @PathVariable(name = "codeReviewsId") Long codeReviewsId,
      @PathVariable(name = "codeReviewCommentsId") Long codeReviewCommentsId,
      @Valid @RequestBody CodeReviewCommentsRequestDto codeReviewCommentsRequestDto,
      @AuthenticationPrincipal UserDetailsImpl userDetails) {

    CodeReviewCommentsResponseDto response = codeReviewCommentsService.updateCodeReviewComment(
        codeReviewsId, codeReviewCommentsId, codeReviewCommentsRequestDto, userDetails.getUser());

    return ResponseEntity.status(HttpStatus.OK)
        .body(new DataResponse<>(200, "코드 리뷰 댓글 수정 완료", response));
  }

  @DeleteMapping("/codereviews/{codeReviewsId}/comments/{codeReviewCommentsId}")
  public ResponseEntity<MessageResponse> deleteCodeReviewComment(
      @PathVariable(name = "codeReviewsId") Long codeReviewsId,
      @PathVariable(name = "codeReviewCommentsId") Long codeReviewCommentsId,
      @AuthenticationPrincipal UserDetailsImpl userDetails) {

    codeReviewCommentsService.deleteCodeReviewComment(codeReviewsId, codeReviewCommentsId,
        userDetails.getUser());

    return ResponseEntity.status(HttpStatus.OK)
        .body(new MessageResponse(200, "코드 리뷰 댓글 삭제 완료"));
  }
}
