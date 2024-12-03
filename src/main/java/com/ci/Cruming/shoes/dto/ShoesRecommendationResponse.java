package com.ci.Cruming.shoes.dto;

import com.ci.Cruming.shoes.entity.Shoes;
import lombok.Getter;

import java.util.List;

@Getter
public class ShoesRecommendationResponse {
    private final List<ShoesDTO> recommendations;

    public ShoesRecommendationResponse(List<Shoes> shoes) {
        this.recommendations = shoes.stream()
            .map(ShoesDTO::fromEntity)
            .toList();
    }
} 