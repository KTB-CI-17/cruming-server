package com.ci.Cruming.timeline.dto;

import com.ci.Cruming.common.constants.Visibility;
import com.ci.Cruming.file.dto.FileResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDate;
import java.util.List;

@Builder
public record TimelineEditInfo(
    Long id,
    String level,
    String content,
    Visibility visibility,
    LocalDate activityAt,
    Location location,
    List<FileResponse> files
) {
    @Schema(description = "암장 위치 상세 정보")
    public record Location(
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
        public Location {}
    }
}
