package team2.pjt12.matchumoney.domain.cardsearch.dto;

import lombok.*;
import team2.pjt12.matchumoney.domain.carddetail.dto.CardOptionDTO;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CardSearchResponseDTO {
    private Long id;             // 카드 ID
    private String name;         // 카드 이름
    private String type;         // 카드 종류 (신용/체크)
    private String imageUrl;     // 카드 이미지 URL
    private String issuer;       // 카드사
    private String annualFee;   // 연회비
    private Integer preMonthMoney; // 전월 실적
    private List<CardOptionDTO> options;
    private Boolean isStarred;
    private Long likeCount;
    private Boolean isLiked;
}
