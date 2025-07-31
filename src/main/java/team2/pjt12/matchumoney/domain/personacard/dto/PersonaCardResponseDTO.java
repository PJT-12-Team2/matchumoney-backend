package team2.pjt12.matchumoney.domain.personacard.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@ApiModel(description = "페르소나 이름과 추천 카드 상품 리스트 DTO")
public class PersonaCardResponseDTO {
    @ApiModelProperty(value = "페르소나 이름", example = "거북이")
    private String personaName;

    @ApiModelProperty(value = "추천 카드 상품 리스트")
    private List<PersonaCardDTO> cards;
}
