package team2.pjt12.matchumoney.domain.saving.codef.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ConnectedIdVO {
    private Long userId;
    private String connectedId;
    private LocalDateTime createdTime;
    private LocalDateTime lastModifiedTime;
}

