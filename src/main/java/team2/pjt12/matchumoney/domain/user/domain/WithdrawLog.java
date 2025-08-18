package team2.pjt12.matchumoney.domain.user.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class WithdrawLog {
    private Long id;
    private String reason;
    private String detail;
    private LocalDateTime createdTime;
    private LocalDateTime lastModifiedTime;

    @Builder
    public WithdrawLog(
            String reason,
            String detail) {
        this.reason = reason;
        this.detail = detail;
        this.createdTime = LocalDateTime.now();
        this.lastModifiedTime = LocalDateTime.now();
    }
}