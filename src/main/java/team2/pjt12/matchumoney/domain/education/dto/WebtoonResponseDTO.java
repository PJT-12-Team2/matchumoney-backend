package team2.pjt12.matchumoney.domain.education.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import team2.pjt12.matchumoney.domain.education.domain.WebtoonVO;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "웹툰 응답 DTO")
public class WebtoonResponseDTO {

    @ApiModelProperty(value = "웹툰 ID", example = "856")
    private Long id;

    @ApiModelProperty(value = "제목", example = "금융교육 웹툰 4화 -중장년편-")
    private String title;

    @ApiModelProperty(value = "첫 번째 이미지 URL", example = "https://...&fileSn=1")
    private String fileDownUrl;

    @ApiModelProperty(value = "두 번째 이미지 URL", example = "https://...&fileSn=2")
    private String secondImageUrl;

    @ApiModelProperty(value = "유형 코드", example = "1")
    private Integer typeCode;

    /**
     * WebtoonVO를 WebtoonResponseDTO로 변환하는 정적 팩토리 메서드
     */
    public static WebtoonResponseDTO from(WebtoonVO webtoon) {
        return WebtoonResponseDTO.builder()
                .id(webtoon.getId())
                .title(webtoon.getTitle())
                .fileDownUrl(webtoon.getFileDownUrl())
                .secondImageUrl(generateSecondImageUrl(webtoon.getFileDownUrl()))
                .typeCode(webtoon.getTypeCode())
                .build();
    }

    /**
     * 첫 번째 이미지 URL에서 두 번째 이미지 URL 생성
     * &fileSn=1 을 &fileSn=2 로 변경
     */
    private static String generateSecondImageUrl(String firstUrl) {
        if (firstUrl != null && firstUrl.contains("&fileSn=1")) {
            return firstUrl.replace("&fileSn=1", "&fileSn=2");
        }
        return firstUrl;
    }


}