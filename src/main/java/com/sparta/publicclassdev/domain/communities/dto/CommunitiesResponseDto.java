package com.sparta.publicclassdev.domain.communities.dto;

import com.sparta.publicclassdev.domain.communities.entity.Communities.Category;
import com.sparta.publicclassdev.domain.communitycomments.dto.CommunityCommentResponseDto;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;

@Getter
public class CommunitiesResponseDto {
    Long id;
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

    public CommunitiesResponseDto(Long id, LocalDateTime createdAt, String title, String content, Category category) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.createdAt = createdAt;
        this.category = category;
    }

    public CommunitiesResponseDto(Long id, String title, String content, LocalDateTime createdAt,Category category, String name, List<CommunityCommentResponseDto> comments) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.category = category;
        this.name = name;
        this.createdAt = createdAt;
        this.comments = comments;
    }
}
