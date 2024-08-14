package com.sparta.publicclassdev.domain.users.dto;

import com.sparta.publicclassdev.domain.users.entity.CalculateTypeEnum;
import lombok.Getter;

@Getter
public class PointRequestDto {
    private int point;
    private CalculateTypeEnum type;
}
