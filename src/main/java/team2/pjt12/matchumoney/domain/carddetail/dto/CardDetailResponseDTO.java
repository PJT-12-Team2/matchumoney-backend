package team2.pjt12.matchumoney.domain.carddetail.dto;

import lombok.Data;
import java.util.List;

@Data
public class CardDetailResponseDTO {
    private int cardProductId;
    private String name;
    private String type;
    private boolean available;
    private int issuerId;
    private String annualFee;
    private Integer preMonthMoney;
    private Boolean onlineOnly;
    private String cardImageUrl;
    private String requestPcUrl;
    private String requestMobileUrl;
    private String annualFeeDetail;
    private String corpPrContainer;
    private String corpPrDetail;
    private String corpTips;
    private long personaId;
    private String issuer;
    private Long userId;
    private boolean liked;
    private int likeCount;
    private boolean starred; // 즐겨찾기 상태

    private List<CardOptionDTO> options;
}