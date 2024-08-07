package com.sparta.publicclassdev.domain.communitycomments.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class CommunityCommentsRequestDto {
    @NotBlank(message = "댓글을 작성해주세요")
    String contents;
}
