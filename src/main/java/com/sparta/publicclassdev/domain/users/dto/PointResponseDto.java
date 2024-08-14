package com.sparta.publicclassdev.domain.users.dto;

import com.sparta.publicclassdev.domain.users.entity.RankEnum;
import lombok.Getter;

@Getter
public class PointResponseDto {
    private int point;
    private RankEnum rank;

    public PointResponseDto(int point, RankEnum rank) {
        this.point = point;
        this.rank = rank;
    }
}
