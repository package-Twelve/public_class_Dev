package com.sparta.publicclassdev.domain.communitycomments.dto;

import lombok.Getter;

@Getter
public class CommunityCommentResponseDto {

    String content;
    Long commentId;

    public CommunityCommentResponseDto(String content, Long communityId) {
        this.content = content;
        this.commentId = communityId;
    }

}
