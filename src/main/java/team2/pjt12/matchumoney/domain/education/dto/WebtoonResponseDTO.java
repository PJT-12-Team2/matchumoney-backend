package team2.pjt12.matchumoney.domain.education.dto;

import team2.pjt12.matchumoney.domain.education.domain.WebtoonVO;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WebtoonResponseDTO {

    private Long id;
    private String title;
    private String fileDownUrl;
    private String secondImageUrl;
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