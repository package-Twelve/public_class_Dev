package com.sparta.publicclassdev.domain.likes.controller;

import com.sparta.publicclassdev.domain.likes.service.LikesService;
import com.sparta.publicclassdev.global.dto.MessageResponse;
import com.sparta.publicclassdev.global.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class LikesController {

  private final LikesService likesService;

  @PostMapping("/codereviews/{codeReviewsId}/comments/{codeReviewCommentsId}/like")
  public ResponseEntity<MessageResponse> setLike(
      @PathVariable(name = "codeReviewsId") Long codeReviewsId,
      @PathVariable(name = "codeReviewCommentsId") Long codeReviewCommentsId,
      @AuthenticationPrincipal UserDetailsImpl userDetails) {

    String responseMessage = likesService.setLike(codeReviewsId, codeReviewCommentsId,
        userDetails.getUser());

    return ResponseEntity.status(HttpStatus.OK)
        .body(new MessageResponse(200, responseMessage));
  }
}
