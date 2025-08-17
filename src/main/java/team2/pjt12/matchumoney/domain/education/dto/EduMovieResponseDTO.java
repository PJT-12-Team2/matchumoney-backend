package team2.pjt12.matchumoney.domain.education.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@ApiModel(description = "교육 영상 정보 DTO")
@NoArgsConstructor
public class EduMovieResponseDTO {
    @ApiModelProperty(value = "교육 영상 id", example = "1")
    private Long id;

    @ApiModelProperty(value = "적금 상품 id", example = "1")
    private String title;

    @ApiModelProperty(value = "간단한 설명", example = "군장병, 군간부를 대상으로 보이스피싱 등 금융사기, 불법사금융, 유사수신, 보험사기 피해사례 및 예방법을 안내하는 동영상입니다.")
    private String smrtnCntnt;

    @ApiModelProperty(value = "출처?", example = "금융감독원")
    private String Institution;
    @ApiModelProperty(value = "영상 링크", example = "https://youtu.be/oLJw2sqLAdo")
    private String link;
}
