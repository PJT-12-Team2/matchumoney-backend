package team2.pjt12.matchumoney.domain.cardsearch.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class CardSearchResponseDTO {
    private Long id;           // 카드 ID
    private String name;       // 카드 이름
    private String type;       // 카드 종류 (신용/체크)
    private String imageUrl;   // 카드 이미지 URL
}
