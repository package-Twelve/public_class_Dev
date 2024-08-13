package com.sparta.publicclassdev.domain.communities.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class CommunitiesUpdateRequestDto {
    String content;

    @Builder
    public CommunitiesUpdateRequestDto(String content) {
        this.content = content;
    }

    public CommunitiesUpdateRequestDto() {
    }
}
