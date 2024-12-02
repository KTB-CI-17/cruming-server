package com.ci.Cruming.shoes.controller;

import com.ci.Cruming.shoes.constants.FootType;
import com.ci.Cruming.shoes.constants.FootWidth;
import com.ci.Cruming.shoes.constants.ClimbingLevel;
import com.ci.Cruming.shoes.dto.ShoesRecommendationResponse;
import com.ci.Cruming.shoes.service.ShoesService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/shoes")
@RequiredArgsConstructor
public class ShoesController {
    private final ShoesService shoesService;

    @GetMapping("/recommendations")
    @Operation(summary = "암벽화 추천", description = "사용자의 발 타입, 발볼, 레벨에 따라 암벽화를 추천합니다.")
    public ResponseEntity<ShoesRecommendationResponse> getRecommendations(
            @RequestParam FootType footType,
            @RequestParam FootWidth footWidth,
            @RequestParam ClimbingLevel level) {
        
        return ResponseEntity.ok(
            new ShoesRecommendationResponse(
                shoesService.getRecommendedShoes(footType, footWidth, level)
            )
        );
    }
} 