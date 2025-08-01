package team2.pjt12.matchumoney.domain.persona.dto;

import lombok.Getter;

@Getter
public final class PersonaSimpleResponseDTO {
    private final String nameKo;
    private final String quote;
    private final String imageUrl;

    public PersonaSimpleResponseDTO(String nameKo, String quote, String imageUrl) {
        this.nameKo = nameKo;
        this.quote = quote;
        this.imageUrl = imageUrl;
    }
}