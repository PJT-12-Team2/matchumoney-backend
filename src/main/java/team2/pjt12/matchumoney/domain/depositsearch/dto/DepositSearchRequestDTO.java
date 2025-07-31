package team2.pjt12.matchumoney.domain.depositsearch.dto;

import lombok.Data;

@Data
public class DepositSearchRequestDTO {
    private String korCoNm;
    private Integer maxLimit;
}