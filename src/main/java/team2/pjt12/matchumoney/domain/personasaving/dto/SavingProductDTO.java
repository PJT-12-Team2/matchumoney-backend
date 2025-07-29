package team2.pjt12.matchumoney.domain.personasaving.dto;

import lombok.Data;

@Data
public class SavingProductDTO {
    private Long id;
    private String name;
    private String bank;
    private String bankInitial;
    private String details;
    private String term;
    private int minAmount;
}
