package team2.pjt12.matchumoney.domain.personadeposit.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PersonaDepositDTO {
    private Long depositId; // deposit_product_id (deposit_product)
    private String bankName; // kor_co_nm (deposit_product)
    private String productName; // fin_prdt_nm (deposit_product)
    private Double basicRate; // intr_rate (deposit_option)
    private Double maxRate; // intr_rate2 (deposit_option)
}
