package team2.pjt12.matchumoney.domain.cardsearch.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "카드 검색 요청 파라미터")
public class CardSearchRequestDTO {
    @ApiModelProperty(value = "신용카드 포함 여부", example = "true")
    private boolean creditCard;

    @ApiModelProperty(value = "체크카드 포함 여부", example = "true")
    private boolean debitCard;

    @ApiModelProperty(value = "선택한 혜택명 목록", example = "[\"교통\", \"카페/디저트\", \"통신\"]")
    private List<String> selectedBenefits;
}
