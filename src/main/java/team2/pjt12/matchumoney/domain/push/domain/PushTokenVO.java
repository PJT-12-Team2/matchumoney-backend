package team2.pjt12.matchumoney.domain.push.domain;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class PushTokenVO {
    private Long id;
    private Long userId;
    private String token;
    private String userAgent;
    private LocalDateTime createdTime;
    private LocalDateTime lastModifiedTime;
    private LocalDateTime lastSeenAt;

    @Builder
    public PushTokenVO(Long userId, String token, String userAgent) {
        this.userId = userId;
        this.token = token;
        this.userAgent = userAgent;
        this.createdTime = LocalDateTime.now();
        this.lastModifiedTime = LocalDateTime.now();
        this.lastSeenAt = LocalDateTime.now();
    }

}
