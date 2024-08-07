package com.sparta.publicclassdev.domain.communities.dto;

import lombok.Getter;

@Getter
public class CommunitiesRankDto {
    String keyword;

    public CommunitiesRankDto(String keyword) {
        this.keyword = keyword;
    }
}
