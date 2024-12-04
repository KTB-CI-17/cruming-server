package com.ci.Cruming.post.dto;

import com.ci.Cruming.common.constants.Category;
import com.ci.Cruming.file.dto.FileResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.List;

@Schema(description = "게시글 수정을 위한 조회 정보 DTO")
@Builder
public record PostEditInfo(
        @Schema(description = "게시글 ID",
                example = "1")
        Long id,

        @Schema(description = "게시글 카테고리",
                example = "PROBLEM")
        Category category,

        @Schema(description = "게시글 제목",
                example = "V3 새로 나온 문제입니다",
                maxLength = 100)
        String title,

        @Schema(description = "게시글 내용",
                example = "시작 홀드는 파란색이고 완등 홀드는 노란색입니다.",
                maxLength = 1000)
        String content,

        @Schema(description = "암장 위치 정보")
        Location location,

        @Schema(description = "문제 난이도",
                example = "V3",
                maxLength = 50)
        String level,

        @Schema(description = "첨부된 파일 목록")
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