package com.sparta.publicclassdev.domain.community.dto;

import com.sparta.publicclassdev.domain.community.entity.Communities.Category;
import com.sparta.publicclassdev.domain.communitycomments.dto.CommunityCommentResponseDto;
import com.sparta.publicclassdev.global.entity.Timestamped;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;

@Getter
public class CommunitiesResponseDto {
    String title;
    String content;
    Category category;
    String name;
    LocalDateTime createdAt;
    List<CommunityCommentResponseDto> comments;

    public CommunitiesResponseDto(String title, String content, Category category) {
        this.title = title;
        this.content = content;
        this.category = category;
    }

    public CommunitiesResponseDto(LocalDateTime createdAt, String title, String content, Category category) {
        this.title = title;
        this.content = content;
        this.createdAt = createdAt;
        this.category = category;
    }

    public CommunitiesResponseDto(String title, String content, Category category, String name, List<CommunityCommentResponseDto> comments) {
        this.title = title;
        this.content = content;
        this.category = category;
        this.name = name;
        this.comments = comments;
    }
}
