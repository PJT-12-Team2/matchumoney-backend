package team2.pjt12.matchumoney.domain.carddetail.dto;

import lombok.Data;

@Data
public class CardOptionDTO {
    private int parsedBenefitId;
    private int cardId2;
    private String title;
    private String category;
    private String benefitType;
    private double value;
    private String conditionText;
    private Integer maxBenefitMonthly;
    private Integer minSpendPerTransaction;
    private Integer preMonthMoneySpecific;
    private String description;
}