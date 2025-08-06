package team2.pjt12.matchumoney.domain.cardrecommendation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KbCardRecommendationResponseDTO {
    private List<KbCardProductDTO> kbCards;
    private int totalCount;
    private String message;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class KbCardProductDTO {
        private Integer cardProductId;
        private String name;
        private String type;
        private String annualFee;
        private Long preMonthMoney;
        private String cardImageUrl;
        private String requestPcUrl;
        private String requestMobileUrl;
        private String annualFeeDetail;
        private String corpPrContainer;
        private String corpTips;
        private String issuer;
    }
}