package team2.pjt12.matchumoney.domain.push.dto;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public final class NewProductResponseDTO {
    private final Long id;
    private final Long personaId;
    private final LocalDateTime createdTime;

    public NewProductResponseDTO(Long id, Long personaId, LocalDateTime createdTime) {
        this.id = id;
        this.personaId = personaId;
        this.createdTime = createdTime;
    }
}
