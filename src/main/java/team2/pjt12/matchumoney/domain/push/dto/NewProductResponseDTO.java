package team2.pjt12.matchumoney.domain.push.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@ApiModel(value = "NewProductResponseDTO", description = "신규 금융 상품 알림 응답 DTO")
public final class NewProductResponseDTO {
    @ApiModelProperty(value = "상품 ID", example = "101")
    private final Long id;

    @ApiModelProperty(value = "대상 페르소나 ID", example = "5")
    private final Long personaId;

    @ApiModelProperty(value = "상품 등록 일시", example = "2025-08-18T15:00:00")
    private final LocalDateTime createdTime;

    public NewProductResponseDTO(Long id, Long personaId, LocalDateTime createdTime) {
        this.id = id;
        this.personaId = personaId;
        this.createdTime = createdTime;
    }
}
