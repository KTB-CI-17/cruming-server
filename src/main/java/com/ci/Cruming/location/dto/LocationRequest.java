package com.ci.Cruming.location.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "암장 위치 정보 요청 DTO")
public record LocationRequest(
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
}