package team2.pjt12.matchumoney.domain.personasaving.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "페르소나 이름과 추천 적금 상품 리스트 DTO")
public class PersonaSavingResponseDTO {
    @ApiModelProperty(value = "페르소나 이름", example = "거북이")
    private String personaName;

    @ApiModelProperty(value = "추천 적금 상품 리스트")
    private List<PersonaSavingDTO> savings;
}
