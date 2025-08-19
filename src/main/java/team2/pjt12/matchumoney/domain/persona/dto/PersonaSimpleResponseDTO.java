package team2.pjt12.matchumoney.domain.persona.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;

@Getter
@ApiModel(description = "사용자의 페르소나 정보 단순화 DTO")
public final class PersonaSimpleResponseDTO {
    @ApiModelProperty(value = "페르소나 이름(한국어)", example = "거북이")
    private final String nameKo;

    @ApiModelProperty(value = "페르소나를 나타내는 문구", example = "안정이 최고의 투자입니다.")
    private final String quote;

    @ApiModelProperty(value = "페르소나 이미지 URL", example = "/src/assets/character_images/turtle.png")
    private final String imageUrl;

    public PersonaSimpleResponseDTO(String nameKo, String quote, String imageUrl) {
        this.nameKo = nameKo;
        this.quote = quote;
        this.imageUrl = imageUrl;
    }
}