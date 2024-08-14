package com.sparta.publicclassdev.domain.communitycomments.controller;

import com.sparta.publicclassdev.domain.communitycomments.dto.CommunityCommentResponseDto;
import com.sparta.publicclassdev.domain.communitycomments.dto.CommunityCommentsRequestDto;
import com.sparta.publicclassdev.domain.communitycomments.service.CommunityCommentsService;
import com.sparta.publicclassdev.domain.users.entity.Users;
import com.sparta.publicclassdev.global.dto.DataResponse;
import com.sparta.publicclassdev.global.dto.MessageResponse;
import com.sparta.publicclassdev.global.security.UserDetailsImpl;
import jakarta.validation.Valid;
import java.util.List;
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
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CommunityCommentsController {

    private final CommunityCommentsService service;

    @PostMapping("/community/{communityId}/comments")
    public ResponseEntity<DataResponse<CommunityCommentResponseDto>> createComment
        (@PathVariable Long communityId, @Valid @RequestBody CommunityCommentsRequestDto requestDto,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        Users user = userDetails.getUser();
        CommunityCommentResponseDto responseDto = service.createComment(communityId, requestDto,
            user);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(new DataResponse<>(HttpStatus.OK.value(), "댓글 생성 완료", responseDto));
    }

    @PutMapping("/community/{communityId}/comments/{commentId}")
    public ResponseEntity<DataResponse<CommunityCommentResponseDto>> updateComment
        (@PathVariable Long communityId, @PathVariable Long commentId,
            @Valid @RequestBody CommunityCommentsRequestDto requestDto,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        Users user = userDetails.getUser();

        CommunityCommentResponseDto responseDto = service.updateComment(communityId, commentId,
            requestDto, user);
        return ResponseEntity.status(HttpStatus.OK)
            .body(new DataResponse<>(HttpStatus.OK.value(), "댓글 수정 완료", responseDto));
    }

    @GetMapping("/community/{coummityId}/comments")
    public ResponseEntity<DataResponse<List<CommunityCommentResponseDto>>> findComments(@PathVariable Long coummityId){
        List<CommunityCommentResponseDto> responseDto = service.findComments(coummityId);

        return ResponseEntity.status(HttpStatus.OK)
            .body(new DataResponse<>(HttpStatus.OK.value(), "댓글 조회 완료", responseDto));
    }

    @DeleteMapping("/community/{communityId}/comments/{commentId}")
    public ResponseEntity<MessageResponse> deleteComment(@PathVariable Long commentId, @PathVariable Long communityId, @AuthenticationPrincipal UserDetailsImpl userDetails){
        Users user = userDetails.getUser();

        service.deleteComment(commentId, communityId, user);
        return ResponseEntity.status(HttpStatus.OK).body(new MessageResponse(HttpStatus.OK.value(), "댓글 삭제 완료"));
    }

}
