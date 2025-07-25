package team2.pjt12.matchumoney.domain.saving.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SavingListItemResponseDTO {
    String id;
    String company;
    String title;
    String max_rate;
    String base_rate;
    String period;
    String company_logo_url;
    Boolean is_starred;
}
