package team2.pjt12.matchumoney.domain.savingdetail.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LikeStatusResponseDTO {
    private boolean liked;
    private int likeCount;
}