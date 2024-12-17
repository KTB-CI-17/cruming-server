package com.ci.Cruming.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Schema(description = "내 정보 수정을 위한 조회 정보 DTO")
@Builder
public record UserEditInfo(
        @Schema(description = "프로필 이미지 URL",
                example = "http://img1.kakaocdn.net/thumb/R640x640.q70/?fname=http://t1.kakaocdn.net/account_images/default_profile.jpeg")
        String profileImageUrl,

        @Schema(description = "사용자 닉네임",
                example = "climber")
        String nickname,

        @Schema(description = "키",
                example = "170")
        Short height,

        @Schema(description = "팔 길이",
                example = "65")
        Short armReach,

        @Schema(description = "한줄 소개",
                example = "클라이밍 초보입니다")
        String intro,

        @Schema(description = "인스타그램 ID",
                example = "climber1225")
        String instagramId,

        @Schema(description = "관심 암장")
        HomeGym homeGym
) {
    @Schema(description = "암장 위치 상세 정보")
    public record HomeGym(
            @Schema(description = "암장 이름",
                    example = "클라이밍 파크")
            String placeName,

            @Schema(description = "암장 주소",
                    example = "서울시 강남구 역삼동 123-45")
            String address,

            @Schema(description = "위도",
                    example = "37.4967363")
            Double latitude,

            @Schema(description = "경도",
                    example = "127.0234832")
            Double longitude
    ) {
        @Builder
        public HomeGym {}
    }
}
