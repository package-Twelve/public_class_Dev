package com.sparta.publicclassdev.domain.communitycomments.dto;

import lombok.Getter;

@Getter
public class CommunityCommentResponseDto {

    String content;
    Long communityId;
    Long commentId;

    public CommunityCommentResponseDto(String content, Long communityId, Long commentId) {
        this.content = content;
        this.communityId = communityId;
        this.commentId = commentId;
    }

}
