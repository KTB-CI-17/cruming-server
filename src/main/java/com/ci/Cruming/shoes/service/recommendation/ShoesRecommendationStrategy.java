package com.ci.Cruming.shoes.service.recommendation;

import com.ci.Cruming.shoes.constants.FootType;
import com.ci.Cruming.shoes.constants.FootWidth;
import com.ci.Cruming.shoes.constants.ClimbingLevel;
import com.ci.Cruming.shoes.entity.Shoes;

import java.util.List;

public interface ShoesRecommendationStrategy {
    List<Shoes> recommendShoes(FootType footType, FootWidth footWidth, ClimbingLevel level);
} 