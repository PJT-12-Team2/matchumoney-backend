package team2.pjt12.matchumoney.domain.mydata.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel(description = "카드 리스트 불러오기 KB 카드 로그인 DTO")
public class KbCardApiRequestDTO {
    @ApiModelProperty(value = "사용자 고유 ID", example = "1", position = 1)
    private Long userId;
    @ApiModelProperty(value = "국민카드 로그인 ID", example = "id", position = 2)
    private String cardId;
    @ApiModelProperty(value = "국민카드 로그인 PW", example = "password", position = 3)
    private String cardPw;
}
