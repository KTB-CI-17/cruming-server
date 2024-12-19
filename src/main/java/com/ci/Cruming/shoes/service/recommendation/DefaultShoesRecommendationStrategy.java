package com.ci.Cruming.shoes.service.recommendation;

import com.ci.Cruming.shoes.constants.FootType;
import com.ci.Cruming.shoes.constants.FootWidth;
import com.ci.Cruming.shoes.constants.ClimbingLevel;
import com.ci.Cruming.shoes.entity.Shoes;
import com.ci.Cruming.shoes.repository.ShoesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DefaultShoesRecommendationStrategy implements ShoesRecommendationStrategy {
    private final ShoesRepository shoesRepository;

    @Override
    public List<Shoes> recommendShoes(FootType footType, FootWidth footWidth, ClimbingLevel level) {
        return shoesRepository.findRecommendedShoes(footType, footWidth, level);
    }
} 