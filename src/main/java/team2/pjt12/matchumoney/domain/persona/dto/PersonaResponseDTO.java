package team2.pjt12.matchumoney.domain.persona.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@ApiModel(description = "사용자의 페르소나 정보 DTO")
public class PersonaResponseDTO {
    @ApiModelProperty(value = "페르소나 ID", example = "1")
    private Long id;

    @ApiModelProperty(value = "페르소나 코드", example = "1")
    private String code;

    @ApiModelProperty(value = "페르소나 이름(한국어)", example = "거북이")
    private String nameKo;

    @ApiModelProperty(value = "페르소나를 나타내는 문구", example = "안정이 최고의 투자입니다.")
    private String quote;

    @ApiModelProperty(value = "사용자 유형", example = "안정 추구형 사용자")
    private String userType;

    @ApiModelProperty(value = "페르소나 설명", example = "당신은 위험을 최소화하며 원금 보장을 최우선으로 생각합니다. 장기적인 목표를 설정하고 꾸준히 나아가는 인내심이 강점입니다.")
    private String description;

    @ApiModelProperty(value = "페르소나 이미지 URL", example = "/src/assets/character_images/turtle.png")
    private String imageUrl;

    @ApiModelProperty(value = "페르소나 관련 태그", example = "[\"도전적\", \"위험 감수\", \"혁신\"]")
    private List<String> tags;

    @ApiModelProperty(value = "추천 상품 유형 목록")
    private List<RecommendationDTO> recommendations;

}
