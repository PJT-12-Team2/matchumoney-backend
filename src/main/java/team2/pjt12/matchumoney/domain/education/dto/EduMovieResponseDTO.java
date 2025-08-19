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
    @ApiModelProperty(value = "교육 영상 id", example = "555")
    private Long id;

    @ApiModelProperty(value = "교육 영상 제목", example = "여이주TV 24 스마트폰으로 주식을 사고팔아보기")
    private String title;

    @ApiModelProperty(value = "간단한 설명", example = "투자할 때 참고할 만한 현명한 매도 매수 방법에 대해 알아봅니다.")
    private String smrtnCntnt;

    @ApiModelProperty(value = "출처", example = "투자자교육협의회")
    private String Institution;

    @ApiModelProperty(value = "영상 링크", example = "https://youtube/oLJw2sqLAdo")
    private String link;
}
