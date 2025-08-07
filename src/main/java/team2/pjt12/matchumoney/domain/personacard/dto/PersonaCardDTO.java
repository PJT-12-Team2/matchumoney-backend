package team2.pjt12.matchumoney.domain.personacard.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import team2.pjt12.matchumoney.domain.carddetail.dto.CardOptionDTO;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@ApiModel(description = "추천 카드 상품 정보 DTO")
public class PersonaCardDTO {
    @ApiModelProperty(value = "카드 ID", example = "1")
    private Long cardId; // 카드 ID (card_product의 card_product_id)

    @ApiModelProperty(value = "카드명", example = "노리체크카드")
    private String cardName; // 카드명 (card_product의 name)

    @ApiModelProperty(value = "카드 이미지 URL", example = "https://d1c5n4ri2guedi.cloudfront.net/card/348/card_img/20581/348card.png")
    private String cardImageUrl; // 카드 이미지 (card_product의 url card_image_url)

    @ApiModelProperty(value = "카드 종류", example = "신용")
    private String cardType; // 카드 종류 (card_product의 type)

    @ApiModelProperty(value = "카드사", example = "KB국민카드")
    private String issuer; // 카드사 (card_product의 issuer)

    @ApiModelProperty(value = "연회비", example = "해외겸용 [15,000]원")
    private String annualFee; // 연회비 (card_product의 annual_fee)

    @ApiModelProperty(value = "전월 실적", example = "300000")
    private Integer preMonthMoney; // 전월 실적 (card_product의 pre_month_money)

    @ApiModelProperty(value = "카드 혜택 옵션 목록")
    private List<CardOptionDTO> options;
}
