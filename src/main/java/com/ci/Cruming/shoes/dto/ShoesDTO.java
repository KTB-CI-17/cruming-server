package com.ci.Cruming.shoes.dto;

import com.ci.Cruming.shoes.entity.Shoes;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ShoesDTO {
    private final Long id;
    private final String photo;
    private final String korean;
    private final String english;
    private final String link;
    private final String tip;

    public static ShoesDTO fromEntity(Shoes shoes) {
        return ShoesDTO.builder()
            .id(shoes.getId())
            .photo(shoes.getPhoto())
            .korean(shoes.getKorean())
            .english(shoes.getEnglish())
            .link(shoes.getLink())
            .tip(shoes.getTip())
            .build();
    }
} 