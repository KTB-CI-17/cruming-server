package com.ci.Cruming.shoes.service;

import com.ci.Cruming.shoes.constants.FootType;
import com.ci.Cruming.shoes.constants.FootWidth;
import com.ci.Cruming.shoes.constants.ClimbingLevel;
import com.ci.Cruming.shoes.entity.Shoes;
import com.ci.Cruming.shoes.service.recommendation.ShoesRecommendationStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ShoesService {
    private final ShoesRecommendationStrategy recommendationStrategy;

    public List<Shoes> getRecommendedShoes(FootType footType, FootWidth footWidth, ClimbingLevel level) {
        return recommendationStrategy.recommendShoes(footType, footWidth, level);
    }
} 