package team2.pjt12.matchumoney.domain.persona.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class PersonaResponseDTO {
    private Long id;
    private String code;
    private String nameKo;
    private String quote;
    private String userType;
    private String description;
    private String imageUrl;
    private List<String> tags;
    private List<RecommendationDTO> recommendations;
}
